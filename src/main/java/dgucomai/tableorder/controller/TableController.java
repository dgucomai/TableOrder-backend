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
  public ResponseEntity<ApiResDto<List<TableSummaryResDto>>> getAllTables() {
    List<TableSummaryResDto> responseData = tableService.getAllTables();
    return ResponseEntity.ok(ApiResDto.success(responseData));
  }

  @GetMapping("/tables/{tableId}")
  public ResponseEntity<ApiResDto<TableDetailResDto>> getTableById(
      @PathVariable Long tableId) {
    TableDetailResDto responseData = tableService.getTableById(tableId);
    return ResponseEntity.ok(ApiResDto.success(responseData));
  }

  @PatchMapping("/tables/{tableId}/clear")
  public ResponseEntity<ApiResDto<TableDetailResDto>> clearTable(
      @PathVariable Long tableId, @RequestParam Long staffId) {
    TableDetailResDto responseData = tableService.clearTable(tableId, staffId);
    return ResponseEntity.ok(ApiResDto.success(responseData));
  }
}
