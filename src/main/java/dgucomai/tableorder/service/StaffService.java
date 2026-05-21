package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.entity.Staff;
import dgucomai.tableorder.domain.enums.PaymentStatus;
import dgucomai.tableorder.dto.req.StaffLoginReqDto;
import dgucomai.tableorder.dto.res.SalesResDto;
import dgucomai.tableorder.dto.res.StaffLoginResDto;
import dgucomai.tableorder.exception.CustomException;
import dgucomai.tableorder.exception.ErrorCode;
import dgucomai.tableorder.repository.PaymentRequestRepository;
import dgucomai.tableorder.repository.table.StaffRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffService {

  private final StaffRepository staffRepository;
  private final PaymentRequestRepository paymentRequestRepository;

  public SalesResDto getSales(Long staffId) {
    staffRepository
        .findById(staffId)
        .orElseThrow(() -> new CustomException(ErrorCode.STAFF_NOT_FOUND));

    Long totalSales = paymentRequestRepository.sumTotalAmountByStatus(PaymentStatus.APPROVED);
    return new SalesResDto(totalSales);
  }

  public StaffLoginResDto staffLogin(StaffLoginReqDto staffLoginReqDto) {
    Staff staff =
        staffRepository
            .findByStaffName(staffLoginReqDto.name())
            .orElseThrow(() -> new CustomException(ErrorCode.STAFF_NOT_FOUND));

    if (!staff.getPassword().equals(staffLoginReqDto.password())) {
      throw new CustomException(ErrorCode.INVALID_STAFF_PASSWORD);
    }

    return new StaffLoginResDto(staff.getStaffName(), staff.getPassword());
  }
}
