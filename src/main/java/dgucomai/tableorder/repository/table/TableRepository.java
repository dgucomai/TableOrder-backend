package dgucomai.tableorder.repository.table;

import dgucomai.tableorder.domain.entity.Tables;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<Tables, Long> {

  Optional<Tables> findByQrToken(String qrToken);
}
