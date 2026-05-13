package dgucomai.tableorder.dto;

import dgucomai.tableorder.domain.GameUsers;
import dgucomai.tableorder.domain.Tables;
import java.time.LocalDateTime;

public record GameUserCreateResDto(
        Long userId,
        Long tableId,
        Integer tableNumber,
        String nickname,
        String phoneLast4,
        int score_1,
        int score_2,
        int score_3,
        int score_4,
        LocalDateTime createdAt) {

    public static GameUserCreateResDto from(GameUsers gameUsers, Tables table) {
        return new GameUserCreateResDto(
                gameUsers.getUserId(),
                table.getTableId(),
                table.getTableNumber(),
                gameUsers.getNickname(),
                getPhoneLast4(gameUsers.getPhoneNumber()),
                gameUsers.getScore1(),
                gameUsers.getScore2(),
                gameUsers.getScore3(),
                gameUsers.getScore4(),
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