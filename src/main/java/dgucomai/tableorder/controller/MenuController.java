package dgucomai.tableorder.controller;

import dgucomai.tableorder.dto.MenuResponse;
import dgucomai.tableorder.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public List<MenuResponse> getAllMenus() {
        return menuService.getAllMenus();
    }
}