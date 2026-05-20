package dgucomai.tableorder.dto.res;

import dgucomai.tableorder.domain.entity.GameUsers;
import dgucomai.tableorder.domain.entity.Tables;
import java.time.LocalDateTime;

public record GameUserCreateResDto(
    Long userId,
    Long tableId,
    Integer tableNumber,
    String nickname,
    String phoneLast4,
    int flappyBirdScore,
    int rockPaperScissorsScore,
    int akoGrowingScore,
    int basketballScore,
    LocalDateTime createdAt) {

  public static GameUserCreateResDto from(GameUsers gameUsers, Tables table) {
    return new GameUserCreateResDto(
        gameUsers.getUserId(),
        table.getTableId(),
        table.getTableNumber(),
        gameUsers.getNickname(),
        getPhoneLast4(gameUsers.getPhoneNumber()),
        gameUsers.getFlappyBirdScore(),
        gameUsers.getRockPaperScissorsScore(),
        gameUsers.getAkoGrowingScore(),
        gameUsers.getBasketballScore(),
        gameUsers.getCreatedAt());
  }

  private static String getPhoneLast4(String phoneNumber) {
    if (phoneNumber == null) {
      return null;
    }
    if (phoneNumber.length() <= 4) {
      return phoneNumber;
    }
    return phoneNumber.substring(phoneNumber.length() - 4);
  }
}
