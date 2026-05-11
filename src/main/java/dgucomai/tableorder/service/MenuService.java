package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.MenuItems;
import dgucomai.tableorder.dto.MenuResDto;
import dgucomai.tableorder.exception.CustomException;
import dgucomai.tableorder.exception.ErrorCode;
import dgucomai.tableorder.repository.MenuItemRepository;
import dgucomai.tableorder.sse.SseEmitterManager;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

  private final MenuItemRepository menuItemRepository;
  private final SseEmitterManager sseEmitterManager;

  public List<MenuResDto> getAllMenus() {
    return menuItemRepository.findAllWithCategory().stream()
        .map(MenuResDto::from)
        .collect(Collectors.toList());
  }

  @Transactional
  public MenuResDto updateSoldOut(Long menuId, boolean isSoldOut) {
    MenuItems menuItems =
        menuItemRepository
            .findById(menuId)
            .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));
    menuItems.updateSoldOut(isSoldOut);
    sseEmitterManager.broadcastSoldOut(menuId, isSoldOut);
    return MenuResDto.from(menuItems);
  }
}
