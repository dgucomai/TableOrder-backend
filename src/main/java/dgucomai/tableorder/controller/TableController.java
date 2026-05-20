package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.res.ApiResDto;
import dgucomai.tableorder.dto.res.TableDetailResDto;
import dgucomai.tableorder.dto.res.TableSummaryResDto;
import dgucomai.tableorder.service.table.TableService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/staff")
public class TableController {

  private final TableService tableService;

  @GetMapping("/tables")
  public List<TableSummaryResDto> getAllTables() {
    return tableService.getAllTables();
  }

  @GetMapping("/tables/{tableId}")
  public TableDetailResDto getTableById(@PathVariable Long tableId) {
    return tableService.getTableById(tableId);
  }

  @PatchMapping("/tables/{tableId}/clear")
  public ResponseEntity<ApiResDto<TableDetailResDto>> clearTable(
      @PathVariable Long tableId, @RequestParam Long staffId) {
    TableDetailResDto responseData = tableService.clearTable(tableId, staffId);
    return ResponseEntity.ok(ApiResDto.success(responseData));
  }
}
