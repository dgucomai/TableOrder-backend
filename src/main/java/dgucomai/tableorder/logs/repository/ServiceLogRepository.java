package dgucomai.tableorder.logs.repository;

import dgucomai.tableorder.logs.entity.ServiceLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceLogRepository extends JpaRepository<ServiceLog, Long> {
  List<ServiceLog> findAllByOrderByCreatedAtDesc();
}
