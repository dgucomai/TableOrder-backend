package dgucomai.tableorder.dto.res;

import java.util.List;

public record GameRankingListResDto(String game, List<GameRankingItemResDto> rankings) {}
