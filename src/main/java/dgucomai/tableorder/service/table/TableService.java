package dgucomai.tableorder.service.table;

import dgucomai.tableorder.domain.entity.Staff;
import dgucomai.tableorder.domain.entity.TableSession;
import dgucomai.tableorder.domain.entity.Tables;
import dgucomai.tableorder.domain.type.TableStatus;
import dgucomai.tableorder.dto.res.TableDetailResDto;
import dgucomai.tableorder.dto.res.TableSummaryResDto;
import dgucomai.tableorder.repository.table.StaffRepository;
import dgucomai.tableorder.repository.table.TableRepository;
import dgucomai.tableorder.repository.table.TableSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TableService {

  private final TableRepository tableRepository;
  private final TableSessionRepository tableSessionRepository;
  private final StaffRepository StaffRepository;

  @Transactional(readOnly = true)
  public List<TableSummaryResDto> getAllTables() {
    return tableRepository.findAll().stream()
        .map(TableSummaryResDto::from)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public TableDetailResDto getTableById(Long tableId) {
    Tables table =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new RuntimeException("테이블을 찾을 수 없습니다."));

    TableSession session = null;
    if (table.getCurrentSessionId() != null) {
      session = tableSessionRepository.findById(table.getCurrentSessionId()).orElse(null);
    }

    return TableDetailResDto.from(table, session);
  }

  @Transactional
  public TableDetailResDto clearTable(Long tableId, Long staffId) {
    Tables table =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new EntityNotFoundException("테이블을 찾을 수 없습니다. ID: " + tableId));

    TableSession currentSession =
        tableSessionRepository
            .findById(table.getCurrentSessionId())
            .orElseThrow(() -> new EntityNotFoundException("현재 세션을 찾을 수 없습니다."));

    if (TableStatus.EMPTY.equals(currentSession.getStatus())) {
      return TableDetailResDto.from(table, currentSession);
    }

    Staff staff =
        StaffRepository.findById(staffId)
            .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다. ID: " + staffId));
    currentSession.setStatus(TableStatus.EMPTY);
    currentSession.setClearedAt(LocalDateTime.now());
    currentSession.setClearedBy(staff);
    tableSessionRepository.save(currentSession);

    TableSession newSession =
        TableSession.builder().table(table).status(TableStatus.EMPTY).tokenCount(0).build();
    TableSession savedNewSession = tableSessionRepository.save(newSession);

    table.setCurrentSessionId(savedNewSession.getSessionId());
    tableRepository.save(table);

    return TableDetailResDto.from(table, savedNewSession);
  }
}
