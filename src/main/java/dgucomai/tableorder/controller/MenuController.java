package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.MenuResDto;
import dgucomai.tableorder.service.MenuService;
import java.util.List;
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
  public List<MenuResDto> getAllMenus() {
    return menuService.getAllMenus();
  }
}
