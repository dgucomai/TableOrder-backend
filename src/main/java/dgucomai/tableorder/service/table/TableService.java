package dgucomai.tableorder.service.table;

import dgucomai.tableorder.domain.entity.Orders;
import dgucomai.tableorder.domain.entity.StaffCall;
import dgucomai.tableorder.domain.entity.Staff;
import dgucomai.tableorder.domain.entity.TableSession;
import dgucomai.tableorder.domain.enums.PaymentStatus;
import dgucomai.tableorder.domain.enums.TableSessionStatus;
import dgucomai.tableorder.domain.type.TableStatus;
import dgucomai.tableorder.dto.res.OrderResDto;
import dgucomai.tableorder.repository.OrdersRepository;
import dgucomai.tableorder.repository.StaffCallRepository;
import dgucomai.tableorder.domain.entity.Tables;
import dgucomai.tableorder.dto.res.TableDetailResDto;
import dgucomai.tableorder.dto.res.TableSummaryResDto;
import dgucomai.tableorder.repository.table.StaffRepository;
import dgucomai.tableorder.repository.table.TableRepository;
import dgucomai.tableorder.repository.table.TableSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TableService {
  private final TableRepository tableRepository;
  private final TableSessionRepository tableSessionRepository;
  private final StaffRepository StaffRepository;
  private final StaffCallRepository staffCallRepository;
  private final OrdersRepository ordersRepository;

  @Transactional(readOnly = true)
  public List<TableSummaryResDto> getAllTables() {
    List<Tables> tables = tableRepository.findAll();

    return tables.stream()
        .map(
            table -> {
              TableStatus calculatedStatus = TableStatus.EMPTY;

              if (table.getCurrentSessionId() != null) {
                TableSession session =
                    tableSessionRepository.findById(table.getCurrentSessionId()).orElse(null);

                if (session != null && session.getStatus() == TableSessionStatus.ACTIVE) {
                  calculatedStatus = TableStatus.IN_USE;

                  boolean hasPaymentPending =
                      ordersRepository.existsBySessionIdAndPaymentStatus(
                          session.getSessionId(), PaymentStatus.PENDING);
                  if (hasPaymentPending) {
                    calculatedStatus = TableStatus.PAYMENT_PENDING;
                  }

                  boolean hasStaffCall =
                      staffCallRepository.existsBySessionIdAndCallTypeAndStatus(
                          session.getSessionId(), "STAFF", "REQUESTED");
                  if (hasStaffCall) {
                    calculatedStatus = TableStatus.STAFF_CALL;
                  }

                  boolean hasDealerCall =
                      staffCallRepository.existsBySessionIdAndCallTypeAndStatus(
                          session.getSessionId(), "DEALER", "REQUESTED");
                  if (hasDealerCall) {
                    calculatedStatus = TableStatus.DEALER_CALL;
                  }
                }
              }

              return TableSummaryResDto.from(table, calculatedStatus);
            })
        .collect(Collectors.toList());
  }

  // 특정 테이블 조회 메서드
  @Transactional(readOnly = true)
  public TableDetailResDto getTableById(Long tableId) {
    Tables table =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new EntityNotFoundException("테이블을 찾을 수 없습니다. ID: " + tableId));

    if (table.getCurrentSessionId() == null) {
      return TableDetailResDto.empty(table, null);
    }

    TableSession session =
        tableSessionRepository
            .findById(table.getCurrentSessionId())
            .orElseThrow(() -> new EntityNotFoundException("현재 세션을 찾을 수 없습니다."));

    // [대원칙] 세션 상태가 CLOSED 라면 무조건 빈 테이블 포맷 반환
    if (session.getStatus() == TableSessionStatus.CLOSED) {
      return TableDetailResDto.empty(table, session.getSessionId());
    }

    // 실시간 주문 및 호출 내역 전역 조회
    List<Orders> dbOrders = ordersRepository.findBySessionId(session.getSessionId());
    List<StaffCall> dbCalls = staffCallRepository.findBySessionId(session.getSessionId());

    // 실시간 테이블 화면 상태(tableStatus) 우선순위 연산
    TableStatus calculatedStatus = TableStatus.IN_USE;

    boolean hasPaymentPending =
        dbOrders.stream().anyMatch(o -> o.getPaymentStatus() == PaymentStatus.PENDING);
    if (hasPaymentPending) calculatedStatus = TableStatus.PAYMENT_PENDING;

    boolean hasStaffCall =
        dbCalls.stream()
            .anyMatch(c -> "STAFF".equals(c.getCallType()) && "REQUESTED".equals(c.getStatus()));
    if (hasStaffCall) calculatedStatus = TableStatus.STAFF_CALL;

    boolean hasDealerCall =
        dbCalls.stream()
            .anyMatch(c -> "DEALER".equals(c.getCallType()) && "REQUESTED".equals(c.getStatus()));
    if (hasDealerCall) calculatedStatus = TableStatus.DEALER_CALL;

    // 5-2. 총 금액 연산 (기존 프로젝트 OrderStatus 상태에 맞춰 REJECTED만 제외하도록 수정)
    int totalAmount =
        dbOrders.stream()
            .filter(
                o -> o.getOrderStatus() != dgucomai.tableorder.domain.enums.OrderStatus.REJECTED)
            .mapToInt(Orders::getTotalAmount)
            .sum();

    List<OrderResDto> orderDtos =
        dbOrders.stream()
            .map(OrderResDto::from) // o -> OrderResDto.from(o) 와 같은 의미입니다.
            .collect(Collectors.toList());

    List<TableDetailResDto.StaffCallResDto> callDtos =
        dbCalls.stream()
            .map(
                c ->
                    new TableDetailResDto.StaffCallResDto(
                        c.getId(),
                        c.getCallType(),
                        c.getMessage(),
                        c.getStatus(),
                        c.getCreatedAt()))
            .collect(Collectors.toList());

    return TableDetailResDto.of(
        table, session, calculatedStatus, totalAmount, orderDtos, callDtos);
  }

  @Transactional
  public TableDetailResDto clearTable(Long tableId, Long staffId) {
    Tables table =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new EntityNotFoundException("테이블을 찾을 수 없습니다. ID: " + tableId));

    TableSession currentSession =
        tableSessionRepository
            .findById(table.getCurrentSessionId())
            .orElseThrow(() -> new EntityNotFoundException("현재 세션을 찾을 수 없습니다."));

    if (currentSession.getStatus() == TableSessionStatus.CLOSED) {
      return getTableById(tableId);
    }

    Staff staff =
        StaffRepository.findById(staffId)
            .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다. ID: " + staffId));
    currentSession.setStatus(TableSessionStatus.CLOSED);
    currentSession.setClearedAt(LocalDateTime.now());
    currentSession.setClearedBy(staff);
    tableSessionRepository.save(currentSession);

    TableSession newSession =
        TableSession.builder()
            .table(table)
            .status(TableSessionStatus.CLOSED)
            .tokenCount(0)
            .startedAt(LocalDateTime.now())
            .build();
    TableSession savedNewSession = tableSessionRepository.save(newSession);

    table.setCurrentSessionId(savedNewSession.getSessionId());
    tableRepository.save(table);

    // 7. (참고) 이후 컨트롤러단 혹은 이벤트 리스너를 통해 TABLE_STATUS_CHANGED SSE를 발행해야 합니다.
    return TableDetailResDto.empty(table, savedNewSession.getSessionId());
  }
}
