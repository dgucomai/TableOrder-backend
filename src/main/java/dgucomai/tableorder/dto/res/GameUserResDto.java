package dgucomai.tableorder.dto.res;

import dgucomai.tableorder.domain.entity.GameUsers;
import java.time.LocalDateTime;

public record GameUserResDto(
    Long userId,
    String nickname,
    String phoneLast4,
    int flappyBirdScore,
    int rockPaperScissorsScore,
    int akoGrowingScore,
    int basketballScore,
    LocalDateTime createdAt) {

  public static GameUserResDto from(GameUsers gameUsers) {
    return new GameUserResDto(
        gameUsers.getUserId(),
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
