package dgucomai.tableorder.dto;

import dgucomai.tableorder.domain.GameUsers;

public record GameUserUpdateResDto(
    Long userId,
    String nickname,
    String phoneLast4,
    int flappyBirdScore,
    int rockPaperScissorsScore,
    int akoGrowingScore,
    int basketballScore) {

  public static GameUserUpdateResDto from(GameUsers gameUsers) {
    return new GameUserUpdateResDto(
        gameUsers.getUserId(),
        gameUsers.getNickname(),
        getPhoneLast4(gameUsers.getPhoneNumber()),
        gameUsers.getFlappyBirdScore(),
        gameUsers.getRockPaperScissorsScore(),
        gameUsers.getAkoGrowingScore(),
        gameUsers.getBasketballScore());
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
