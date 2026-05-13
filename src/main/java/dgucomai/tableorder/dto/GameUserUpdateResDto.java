package dgucomai.tableorder.dto;

import dgucomai.tableorder.domain.GameUsers;

public record GameUserUpdateResDto(
    Long userId,
    String nickname,
    String phoneLast4,
    int score_1,
    int score_2,
    int score_3,
    int score_4) {

  public static GameUserUpdateResDto from(GameUsers gameUsers) {
    return new GameUserUpdateResDto(
        gameUsers.getUserId(),
        gameUsers.getNickname(),
        getPhoneLast4(gameUsers.getPhoneNumber()),
        gameUsers.getScore1(),
        gameUsers.getScore2(),
        gameUsers.getScore3(),
        gameUsers.getScore4());
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
