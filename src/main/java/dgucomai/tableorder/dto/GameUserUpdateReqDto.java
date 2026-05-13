package dgucomai.tableorder.dto;

public record GameUserUpdateReqDto(
    String nickname,
    String phoneNumber,
    Integer score_1,
    Integer score_2,
    Integer score_3,
    Integer score_4) {

  public boolean hasNoUpdateFields() {
    return nickname == null
        && phoneNumber == null
        && score_1 == null
        && score_2 == null
        && score_3 == null
        && score_4 == null;
  }
}
