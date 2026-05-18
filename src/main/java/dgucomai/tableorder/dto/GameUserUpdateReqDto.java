package dgucomai.tableorder.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record GameUserUpdateReqDto(
        String nickname,
        String phoneNumber,

        @JsonAlias({"score_1", "flappy_bird_score", "flappyBirdScore"})
        Integer flappyBirdScore,

        @JsonAlias({"score_2", "rock_paper_scissors_score", "rockPaperScissorsScore"})
        Integer rockPaperScissorsScore,

        @JsonAlias({"score_3", "ako_growing_score", "akoGrowingScore"})
        Integer akoGrowingScore,

        @JsonAlias({"score_4", "basketball_score", "basketballScore"})
        Integer basketballScore
) {

  public boolean hasNoUpdateFields() {
    return nickname == null
            && phoneNumber == null
            && flappyBirdScore == null
            && rockPaperScissorsScore == null
            && akoGrowingScore == null
            && basketballScore == null;
  }
}