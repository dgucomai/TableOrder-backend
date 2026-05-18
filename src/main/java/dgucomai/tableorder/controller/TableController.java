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
  public List<TableSummaryResponseDto> getAllTables() {
    return tableService.getAllTables();
  }

  @GetMapping("/tables/{tableId}")
  public TableDetailResponseDto getTableById(@PathVariable Long tableId) {
    return tableService.getTableById(tableId);
  }

  @PatchMapping("/tables/{tableId}/clear")
  public ResponseEntity<ApiResDto<TableDetailResponseDto>> clearTable(
      @PathVariable Long tableId, @RequestParam Long staffId) {
    TableDetailResponseDto responseData = tableService.clearTable(tableId, staffId);
    ApiResDto<TableDetailResponseDto> body = ApiResDto.success(responseData);
    return ResponseEntity.ok(body);
  }
}
