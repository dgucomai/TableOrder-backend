package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.StaffCall;
import dgucomai.tableorder.dto.StaffCallReqDto;
import dgucomai.tableorder.repository.StaffCallRepository;
import dgucomai.tableorder.sse.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
  private final StaffCallRepository staffCallRepository;
  private final SseService sseService;

  public void callStaff(StaffCallReqDto dto) {
    if (!StringUtils.hasText(dto.message())) {
      throw new IllegalArgumentException("메시지가 비어 있습니다.");
    }

    Long tableId = 1L;

    StaffCall staffCall = new StaffCall(tableId, dto.message());
    staffCallRepository.save(staffCall);

    sseService.sendStaffCallEvent(tableId, staffCall.getMessage(), staffCall.getStatus());
  }
}
