package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.GameUsers;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameUserRepository extends JpaRepository<GameUsers, Long> {

  List<GameUsers> findTop5ByOrderByScore1DescCreatedAtAsc();

  List<GameUsers> findTop5ByOrderByScore2DescCreatedAtAsc();

  List<GameUsers> findTop5ByOrderByScore3DescCreatedAtAsc();

  List<GameUsers> findTop5ByOrderByScore4DescCreatedAtAsc();
}
