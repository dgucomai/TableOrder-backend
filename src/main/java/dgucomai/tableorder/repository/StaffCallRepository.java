package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.StaffCall;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository를 상속받으면 기본적인 저장(save), 조회(findById) 등을
 * 직접 코드로 짜지 않아도 스프링이 알아서 만들어줍니다.
 */
public interface StaffCallRepository extends JpaRepository<StaffCall, Long> {
    // 지금은 기본 기능만 사용하므로 내용을 비워둬도 작동합니다.
}