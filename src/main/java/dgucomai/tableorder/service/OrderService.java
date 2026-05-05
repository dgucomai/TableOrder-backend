package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.StaffCall;
import dgucomai.tableorder.repository.StaffCallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // 이 클래스가 서비스 계층임을 스프링에 알립니다.
@Transactional // 작업 도중 에러가 나면 DB를 이전 상태로 되돌리는(Rollback) 안전장치입니다.
@RequiredArgsConstructor // 아래의 Repository를 자동으로 연결(주입)해줍니다.
public class OrderService {

    private final StaffCallRepository staffCallRepository;

    /**
     * 직원 호출 로직
     * 1. 호출 정보를 담은 객체를 만들고
     * 2. Repository를 통해 DB에 저장합니다.
     */
    public void callStaff(Long tableId) {
        // 엔티티 객체 생성 (현재 테이블 번호 전달)
        StaffCall staffCall = new StaffCall(tableId);

        // DB 창고에 저장
        staffCallRepository.save(staffCall);
    }
}