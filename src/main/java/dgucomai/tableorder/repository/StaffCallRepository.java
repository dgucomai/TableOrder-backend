package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.StaffCall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffCallRepository extends JpaRepository<StaffCall, Long> {
}