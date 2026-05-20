package dgucomai.tableorder.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResDto {
  private Long tableId;
  private Integer tableNumber;
  private Integer tokenCount;
}
