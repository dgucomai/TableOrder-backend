package dgucomai.tableorder.dto;

import dgucomai.tableorder.domain.GameUsers;

public record GameRankingItemResDto(
    int rank, Long userId, String nickname, String phoneLast4, int score) {

  public static GameRankingItemResDto from(GameUsers gameUsers, int rank, int gameIndex) {
    return new GameRankingItemResDto(
        rank,
        gameUsers.getUserId(),
        gameUsers.getNickname(),
        getPhoneLast4(gameUsers.getPhoneNumber()),
        gameUsers.getScoreByGameIndex(gameIndex));
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
