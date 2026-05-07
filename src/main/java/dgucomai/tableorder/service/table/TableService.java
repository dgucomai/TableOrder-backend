package dgucomai.tableorder.service.table;

import dgucomai.tableorder.domain.entity.TableEntity;
import dgucomai.tableorder.domain.response.table.TableDetailResponseDto;
import dgucomai.tableorder.domain.response.table.TableSummaryResponseDto;
import dgucomai.tableorder.repository.table.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableService {
    private final TableRepository tableRepository; //repository 연결

    //전체 테이블 조회 메서드
    @Transactional(readOnly = true)
    public List<TableSummaryResponseDto> getAllTables() {
        return tableRepository.findAll()
                .stream()
                .map(TableSummaryResponseDto::from)
                .collect(Collectors.toList());
    }

    //특정 테이블 조회 메서드
    @Transactional(readOnly = true)
    public TableDetailResponseDto getTableById(Long tableId) {
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("테이블을 찾을 수 없습니다."));
        return TableDetailResponseDto.from(table);
    }
}
