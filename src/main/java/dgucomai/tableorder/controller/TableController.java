package dgucomai.tableorder.controller;

import dgucomai.tableorder.domain.response.table.TableDetailResponseDto;
import dgucomai.tableorder.domain.response.table.TableSummaryResponseDto;
import dgucomai.tableorder.dto.ApiResDto;
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
  public ResponseEntity<ApiResDto<List<TableSummaryResponseDto>>> getAllTables() {
    List<TableSummaryResponseDto> responseData = tableService.getAllTables();
    return ResponseEntity.ok(ApiResDto.success(responseData));
  }

  @GetMapping("/tables/{tableId}")
  public ResponseEntity<ApiResDto<TableDetailResponseDto>> getTableById(
      @PathVariable Long tableId) {
    TableDetailResponseDto responseData = tableService.getTableById(tableId);
    return ResponseEntity.ok(ApiResDto.success(responseData));
  }

  @PatchMapping("/tables/{tableId}/clear")
  public ResponseEntity<ApiResDto<TableDetailResponseDto>> clearTable(
      @PathVariable Long tableId, @RequestParam Long staffId) {
    TableDetailResponseDto responseData = tableService.clearTable(tableId, staffId);
    ApiResDto<TableDetailResponseDto> body = ApiResDto.success(responseData);
    return ResponseEntity.ok(body);
  }
}
