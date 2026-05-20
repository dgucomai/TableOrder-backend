package dgucomai.tableorder.repository.table; // 패키지 경로는 프로젝트에 맞춰 조정하세요

import dgucomai.tableorder.domain.entity.Staff;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
  Optional<Staff> findByStaffName(String staffName);
}
