package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.MenuResDto;
import dgucomai.tableorder.service.MenuService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

  private final MenuService menuService;

  @GetMapping
  public Map<String, Object> getAllMenus() {
    List<MenuResDto> menus = menuService.getAllMenus();

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("success", true);
    response.put("code", "OK");
    response.put("message", "메뉴 목록 조회 성공");
    response.put("data", Map.of("menus", menus));

    return response;
  }
}
