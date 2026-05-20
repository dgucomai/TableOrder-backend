package dgucomai.tableorder.dto.req;

public record GameUserUpdateReqDto(
    String nickname,
    String phoneNumber,
    Integer flappyBirdScore,
    Integer rockPaperScissorsScore,
    Integer akoGrowingScore,
    Integer basketballScore) {

  public boolean hasNoUpdateFields() {
    return nickname == null
        && phoneNumber == null
        && flappyBirdScore == null
        && rockPaperScissorsScore == null
        && akoGrowingScore == null
        && basketballScore == null;
  }
}
