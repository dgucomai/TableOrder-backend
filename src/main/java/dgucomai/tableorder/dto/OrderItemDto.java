package dgucomai.tableorder.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemDto {
    private String menuName;
    private int quantity;
    private int price;
}