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

    @Column(name = "score_1", nullable = false)
    private int score1;

    @Column(name = "score_2", nullable = false)
    private int score2;

    @Column(name = "score_3", nullable = false)
    private int score3;

    @Column(name = "score_4", nullable = false)
    private int score4;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public GameUsers(Long sessionId, String nickname, String phoneNumber) {
        this.sessionId = sessionId;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.score1 = 0;
        this.score2 = 0;
        this.score3 = 0;
        this.score4 = 0;
        this.createdAt = LocalDateTime.now();
    }

    public void update(
            String nickname,
            String phoneNumber,
            Integer score1,
            Integer score2,
            Integer score3,
            Integer score4) {
        if (nickname != null) {
            this.nickname = nickname;
        }

        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }

        if (score1 != null) {
            this.score1 = score1;
        }

        if (score2 != null) {
            this.score2 = score2;
        }

        if (score3 != null) {
            this.score3 = score3;
        }

        if (score4 != null) {
            this.score4 = score4;
        }
    }

    public int getScoreByGameIndex(int gameIndex) {
        return switch (gameIndex) {
            case 1 -> score1;
            case 2 -> score2;
            case 3 -> score3;
            case 4 -> score4;
            default -> 0;
        };
    }
}