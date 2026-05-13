package dgucomai.tableorder.controller;

import dgucomai.tableorder.domain.response.table.TableDetailResponseDto;
import dgucomai.tableorder.domain.response.table.TableSummaryResponseDto;
import dgucomai.tableorder.service.table.TableService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/staff")
public class TableController {
  private final TableService tableService;

  @GetMapping("/tables")
  public List<TableSummaryResponseDto> getAllTables() {
    return tableService.getAllTables();
  }

  @GetMapping("/tables/{tableId}")
  public TableDetailResponseDto getTableById(@PathVariable Long tableId) {
    return tableService.getTableById(tableId);
  }
}
