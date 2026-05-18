package dgucomai.tableorder.service.table;

import dgucomai.tableorder.domain.entity.Staff;
import dgucomai.tableorder.domain.entity.TableEntity;
import dgucomai.tableorder.domain.entity.TableSession;
import dgucomai.tableorder.domain.response.table.TableDetailResponseDto;
import dgucomai.tableorder.domain.response.table.TableSummaryResponseDto;
import dgucomai.tableorder.domain.type.TableStatus;
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
  private final TableRepository tableRepository; // repository 연결
  private final TableSessionRepository tableSessionRepository;
  private final StaffRepository StaffRepository;

  // 전체 테이블 조회 메서드
  @Transactional(readOnly = true)
  public List<TableSummaryResponseDto> getAllTables() {
    return tableRepository.findAll().stream()
        .map(TableSummaryResponseDto::from)
        .collect(Collectors.toList());
  }

  // 특정 테이블 조회 메서드
  @Transactional(readOnly = true)
  public TableDetailResponseDto getTableById(Long tableId) {
    TableEntity table =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new RuntimeException("테이블을 찾을 수 없습니다."));

    TableSession session = null;
    if (table.getCurrentSessionId() != null) {
      session = tableSessionRepository.findById(table.getCurrentSessionId()).orElse(null);
    }

    return TableDetailResponseDto.from(table, session);
  }

  @Transactional
  public TableDetailResponseDto clearTable(Long tableId, Long staffId) {
    // 1. tableId로 물리 테이블 조회
    TableEntity table =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new EntityNotFoundException("테이블을 찾을 수 없습니다. ID: " + tableId));

    // 2. tables.current_session_id로 현재 세션 조회
    TableSession currentSession =
        tableSessionRepository
            .findById(table.getCurrentSessionId())
            .orElseThrow(() -> new EntityNotFoundException("현재 세션을 찾을 수 없습니다."));

    // 3. 멱등성 검사: 현재 세션이 이미 CLOSED이면 추가 변경 없이 종료
    if (TableStatus.EMPTY.equals(currentSession.getStatus())) {
      return TableDetailResponseDto.from(table, currentSession);
    }

    // 4. 기존 ACTIVE 세션을 CLOSED로 종료 처리
    // (주문/결제/호출 상태는 변경하지 않고 그대로 보존)
    Staff staff =
        StaffRepository.findById(staffId)
            .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다. ID: " + staffId));
    currentSession.setStatus(TableStatus.EMPTY);
    currentSession.setClearedAt(LocalDateTime.now());
    currentSession.setClearedBy(staff); // table_sessions.cleared_by에 기록 [cite: 270]
    tableSessionRepository.save(currentSession);

    // 5. 새 table_sessions 생성
    TableSession newSession =
        TableSession.builder().table(table).status(TableStatus.EMPTY).tokenCount(0).build();
    TableSession savedNewSession = tableSessionRepository.save(newSession);

    // 6. 물리 테이블의 current_session_id를 새 session_id로 갱신
    table.setCurrentSessionId(savedNewSession.getSessionId());
    tableRepository.save(table);

    // 7. (참고) 이후 컨트롤러단 혹은 이벤트 리스너를 통해 TABLE_STATUS_CHANGED SSE를 발행해야 합니다.
    return TableDetailResponseDto.from(table, savedNewSession);
  }
}
