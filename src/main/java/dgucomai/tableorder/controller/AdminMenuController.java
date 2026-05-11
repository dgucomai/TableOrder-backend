package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.ApiResDto;
import dgucomai.tableorder.dto.MenuResDto;
import dgucomai.tableorder.dto.SoldOutReqDto;
import dgucomai.tableorder.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminMenuController {

  private final MenuService menuService;

  @PatchMapping("/menu-items/{menuId}/sold-out")
  public ResponseEntity<ApiResDto<MenuResDto>> toggleSoldOut(
      @PathVariable Long menuId, @RequestBody SoldOutReqDto request) {
    MenuResDto response = menuService.updateSoldOut(menuId, request.isSoldOut());
    return ResponseEntity.ok(ApiResDto.success(response));
  }
}
