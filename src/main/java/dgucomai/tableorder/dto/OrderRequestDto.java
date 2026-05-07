package dgucomai.tableorder.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class OrderRequestDto {
    private Long tableId;
    private List<OrderItemDto> items;
}