package dgucomai.tableorder.dto;

import java.util.List;

public record GameRankingListResDto(String game, List<GameRankingItemResDto> rankings) {}
