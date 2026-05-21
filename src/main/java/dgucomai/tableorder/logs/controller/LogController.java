package dgucomai.tableorder.logs.controller;

import dgucomai.tableorder.dto.res.ApiResDto;
import dgucomai.tableorder.logs.dto.ServiceLogResDto;
import dgucomai.tableorder.logs.dto.TechLogResDto;
import dgucomai.tableorder.logs.service.LogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class LogController {

  private final LogService logService;

  @GetMapping("/service")
  public ResponseEntity<ApiResDto<List<ServiceLogResDto>>> getServiceLogs() {
    List<ServiceLogResDto> data =
        logService.findAllServiceLogs().stream().map(ServiceLogResDto::from).toList();
    return ResponseEntity.ok(ApiResDto.success(data));
  }

  @GetMapping("/tech")
  public ResponseEntity<ApiResDto<List<TechLogResDto>>> getTechLogs() {
    List<TechLogResDto> data =
        logService.findAllTechLogs().stream().map(TechLogResDto::from).toList();
    return ResponseEntity.ok(ApiResDto.success(data));
  }
}
