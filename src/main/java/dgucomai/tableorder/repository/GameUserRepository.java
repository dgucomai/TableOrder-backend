package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.entity.GameUsers;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameUserRepository extends JpaRepository<GameUsers, Long> {

  List<GameUsers> findTop5ByOrderByFlappyBirdScoreDescCreatedAtAsc();

  List<GameUsers> findTop5ByOrderByRockPaperScissorsScoreDescCreatedAtAsc();

  List<GameUsers> findTop5ByOrderByAkoGrowingScoreDescCreatedAtAsc();

  List<GameUsers> findTop5ByOrderByBasketballScoreDescCreatedAtAsc();
}
