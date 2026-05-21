package dgucomai.tableorder.logs.repository;

import dgucomai.tableorder.logs.entity.TechLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechLogRepository extends JpaRepository<TechLog, Long> {
  List<TechLog> findAllByOrderByCreatedAtDesc();
}
