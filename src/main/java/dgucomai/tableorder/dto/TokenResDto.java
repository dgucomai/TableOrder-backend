package dgucomai.tableorder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResDto {
  private Long tableId;
  private Integer tableNumber;
  private Integer tokenCount;
}
