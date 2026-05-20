package dgucomai.tableorder.repository;

import java.util.List;
import dgucomai.tableorder.domain.entity.StaffCall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffCallRepository extends JpaRepository<StaffCall, Long> {
  boolean existsBySessionIdAndCallTypeAndStatus(Long sessionId, String callType, String status);

  // ⭐️ 이 메서드를 새로 추가해 주세요! 현재 세션의 모든 호출 목록을 가져옵니다.
  List<StaffCall> findBySessionId(Long sessionId);
}
