package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.Tables;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameTableRepository extends JpaRepository<Tables, Long> {

  Optional<Tables> findByQrToken(String qrToken);
}
