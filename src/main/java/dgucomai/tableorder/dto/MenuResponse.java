package dgucomai.tableorder.dto;

import dgucomai.tableorder.domain.MenuItem;

public record MenuResponse(
    Long menuId,
    Long categoryId,
    String categoryName,
    String menuName,
    int price,
    String description,
    String imageUrl,
    boolean isSoldOut) {
  public static MenuResponse from(MenuItem menuItem) {
    return new MenuResponse(
        menuItem.getId(),
        menuItem.getCategory().getId(),
        menuItem.getCategory().getCategoryName(),
        menuItem.getMenuName(),
        menuItem.getPrice(),
        menuItem.getDescription(),
        menuItem.getImageUrl(),
        menuItem.isSoldOut());
  }
}
