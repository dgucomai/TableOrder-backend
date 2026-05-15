package dgucomai.tableorder.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_users")
@Getter
@NoArgsConstructor
public class GameUsers {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "session_id", nullable = false)
  private Long sessionId;

  @Column(name = "nickname", nullable = false)
  private String nickname;

  @Column(name = "phone_number", nullable = false)
  private String phoneNumber;

  @Column(name = "flappy_bird_score", nullable = false)
  private int flappyBirdScore;

  @Column(name = "rock_paper_scissors_score", nullable = false)
  private int rockPaperScissorsScore;

  @Column(name = "ako_growing_score", nullable = false)
  private int akoGrowingScore;

  @Column(name = "basketball_score", nullable = false)
  private int basketballScore;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public GameUsers(Long sessionId, String nickname, String phoneNumber) {
    this.sessionId = sessionId;
    this.nickname = nickname;
    this.phoneNumber = phoneNumber;
    this.flappyBirdScore = 0;
    this.rockPaperScissorsScore = 0;
    this.akoGrowingScore = 0;
    this.basketballScore = 0;
    this.createdAt = LocalDateTime.now();
  }

  public void update(
      String nickname,
      String phoneNumber,
      Integer flappyBirdScore,
      Integer rockPaperScissorsScore,
      Integer akoGrowingScore,
      Integer basketballScore) {
    if (nickname != null) {
      this.nickname = nickname;
    }

    if (phoneNumber != null) {
      this.phoneNumber = phoneNumber;
    }

    if (flappyBirdScore != null) {
      this.flappyBirdScore = flappyBirdScore;
    }

    if (rockPaperScissorsScore != null) {
      this.rockPaperScissorsScore = rockPaperScissorsScore;
    }

    if (akoGrowingScore != null) {
      this.akoGrowingScore = akoGrowingScore;
    }

    if (basketballScore != null) {
      this.basketballScore = basketballScore;
    }
  }

  public int getScoreByGameIndex(int gameIndex) {
    return switch (gameIndex) {
      case 1 -> flappyBirdScore;
      case 2 -> rockPaperScissorsScore;
      case 3 -> akoGrowingScore;
      case 4 -> basketballScore;
      default -> 0;
    };
  }
}
