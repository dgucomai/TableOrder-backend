package dgucomai.tableorder.dto.res;

import dgucomai.tableorder.domain.entity.MenuItems;

public record MenuResDto(
    Long menuId,
    Long categoryId,
    String categoryName,
    String menuName,
    int price,
    String description,
    String imageUrl,
    boolean isSoldOut) {

  public static MenuResDto from(MenuItems menuItems) {
    return new MenuResDto(
        menuItems.getId(),
        menuItems.getCategory().getId(),
        menuItems.getCategory().getCategoryName(),
        menuItems.getMenuName(),
        menuItems.getPrice(),
        menuItems.getDescription(),
        menuItems.getImageUrl(),
        menuItems.isSoldOut());
  }
}
