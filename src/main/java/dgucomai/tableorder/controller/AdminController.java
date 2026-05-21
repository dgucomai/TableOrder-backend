package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.req.StaffLoginReqDto;
import dgucomai.tableorder.dto.res.ApiResDto;
import dgucomai.tableorder.dto.res.SalesResDto;
import dgucomai.tableorder.dto.res.StaffLoginResDto;
import dgucomai.tableorder.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {

  private final StaffService staffService;

  @GetMapping("/admin/sales")
  public ResponseEntity<ApiResDto<SalesResDto>> getSales(@RequestParam Long staffId) {
    return ResponseEntity.ok(ApiResDto.success(staffService.getSales(staffId)));
  }

  @PostMapping("/staff/login")
  public ResponseEntity<ApiResDto<StaffLoginResDto>> staffLogin(
      @Valid @RequestBody StaffLoginReqDto staffLoginReqDto) {
    return ResponseEntity.ok(ApiResDto.success(staffService.staffLogin(staffLoginReqDto)));
  }
}
