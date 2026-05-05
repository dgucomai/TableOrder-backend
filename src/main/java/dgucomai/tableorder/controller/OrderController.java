package dgucomai.tableorder.controller;

import dgucomai.tableorder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // 이 클래스가 JSON 형태의 데이터를 주고받는 API 입구임을 선언합니다.
@RequestMapping("/api") // 이 컨트롤러의 모든 주소는 /api로 시작합니다.
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService; // 일을 시킬 서비스 클래스를 가져옵니다.

    /**
     * 직원 호출 API
     * POST 방식으로 /api/staff-call 주소에 요청이 오면 실행됩니다.
     * 예: /api/staff-call?tableId=3
     */
    @PostMapping("/staff-call")
    public ResponseEntity<String> staffCall(@RequestParam Long tableId) {
        // 서비스에게 테이블 번호를 넘기며 처리를 맡깁니다.
        orderService.callStaff(tableId);

        // 처리가 끝나면 성공 메시지를 프론트엔드(방문자 화면)에 보냅니다.
        return ResponseEntity.ok(tableId + "번 테이블의 호출이 접수되었습니다.");
    }
}