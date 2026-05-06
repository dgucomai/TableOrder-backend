package dgucomai.tableorder.service;

import dgucomai.tableorder.dto.MenuResponse;
import dgucomai.tableorder.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    public List<MenuResponse> getAllMenus() {
        return menuItemRepository.findAllWithCategory().stream()

                .map(MenuResponse::from)

                .collect(Collectors.toList());
    }
}