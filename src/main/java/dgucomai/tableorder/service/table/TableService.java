package dgucomai.tableorder.service.table;

import dgucomai.tableorder.domain.Orders;
import dgucomai.tableorder.domain.StaffCall;
import dgucomai.tableorder.domain.entity.Staff;
import dgucomai.tableorder.domain.entity.TableEntity;
import dgucomai.tableorder.domain.entity.TableSession;
import dgucomai.tableorder.domain.enums.PaymentStatus;
import dgucomai.tableorder.domain.enums.TableSessionStatus;
import dgucomai.tableorder.domain.response.table.TableDetailResponseDto;
import dgucomai.tableorder.domain.response.table.TableSummaryResponseDto;
import dgucomai.tableorder.domain.type.TableStatus;
import dgucomai.tableorder.dto.OrderResDto;
import dgucomai.tableorder.repository.OrdersRepository;
import dgucomai.tableorder.repository.StaffCallRepository;
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
  public List<TableSummaryResponseDto> getAllTables() {
    List<TableEntity> tables = tableRepository.findAll();

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

              return TableSummaryResponseDto.from(table, calculatedStatus);
            })
        .collect(Collectors.toList());
  }

  // 특정 테이블 조회 메서드
  // 2. 특정 테이블 상세 조회 (가이드라인 상세 연산 로직 완벽 적용)
  @Transactional(readOnly = true)
  public TableDetailResponseDto getTableById(Long tableId) {
    TableEntity table =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new EntityNotFoundException("테이블을 찾을 수 없습니다. ID: " + tableId));

    if (table.getCurrentSessionId() == null) {
      return TableDetailResponseDto.empty(table, null);
    }

    TableSession session =
        tableSessionRepository
            .findById(table.getCurrentSessionId())
            .orElseThrow(() -> new EntityNotFoundException("현재 세션을 찾을 수 없습니다."));

    // [대원칙] 세션 상태가 CLOSED 라면 무조건 빈 테이블 포맷 반환
    if (session.getStatus() == TableSessionStatus.CLOSED) {
      return TableDetailResponseDto.empty(table, session.getSessionId());
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

    List<TableDetailResponseDto.StaffCallResDto> callDtos =
        dbCalls.stream()
            .map(
                c ->
                    new TableDetailResponseDto.StaffCallResDto(
                        c.getCallId(),
                        c.getCallType(),
                        c.getMessage(),
                        c.getStatus(),
                        c.getCreatedAt()))
            .collect(Collectors.toList());

    return TableDetailResponseDto.of(
        table, session, calculatedStatus, totalAmount, orderDtos, callDtos);
  }

  @Transactional
  public TableDetailResponseDto clearTable(Long tableId, Long staffId) {
    // 1. tableId로 물리 테이블 조회
    TableEntity table =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new EntityNotFoundException("테이블을 찾을 수 없습니다. ID: " + tableId));

    // 2. tables.current_session_id로 현재 세션 조회
    TableSession currentSession =
        tableSessionRepository
            .findById(table.getCurrentSessionId())
            .orElseThrow(() -> new EntityNotFoundException("현재 세션을 찾을 수 없습니다."));

    // 3. 멱등성 검사: 현재 세션이 이미 CLOSED이면 추가 변경 없이 종료
    if (currentSession.getStatus() == TableSessionStatus.CLOSED) {
      return getTableById(tableId);
    }

    // 4. 기존 ACTIVE 세션을 CLOSED로 종료 처리
    // (주문/결제/호출 상태는 변경하지 않고 그대로 보존)
    Staff staff =
        StaffRepository.findById(staffId)
            .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다. ID: " + staffId));
    currentSession.setStatus(TableSessionStatus.CLOSED);
    currentSession.setClearedAt(LocalDateTime.now());
    currentSession.setClearedBy(staff);
    tableSessionRepository.save(currentSession);

    // 5. 새 table_sessions 생성
    TableSession newSession =
        TableSession.builder()
            .table(table)
            .status(TableSessionStatus.ACTIVE) // CLOSED에서 ACTIVE로 수정!
            .tokenCount(0)
            .startedAt(LocalDateTime.now()) // 새 세션 시작 시간 부여
            .build();
    TableSession savedNewSession = tableSessionRepository.save(newSession);

    // 6. 물리 테이블의 current_session_id를 새 session_id로 갱신
    table.setCurrentSessionId(savedNewSession.getSessionId());
    tableRepository.save(table);

    // 7. (참고) 이후 컨트롤러단 혹은 이벤트 리스너를 통해 TABLE_STATUS_CHANGED SSE를 발행해야 합니다.
    return TableDetailResponseDto.empty(table, savedNewSession.getSessionId());
  }
}
