package dgucomai.tableorder.service.table;

import dgucomai.tableorder.domain.entity.Orders;
import dgucomai.tableorder.domain.entity.Staff;
import dgucomai.tableorder.domain.entity.StaffCall;
import dgucomai.tableorder.domain.entity.TableSession;
import dgucomai.tableorder.domain.entity.Tables;
import dgucomai.tableorder.domain.enums.OrderStatus;
import dgucomai.tableorder.domain.enums.PaymentStatus;
import dgucomai.tableorder.domain.enums.TableSessionStatus;
import dgucomai.tableorder.domain.type.TableStatus;
import dgucomai.tableorder.dto.res.OrderResDto;
import dgucomai.tableorder.dto.res.TableDetailResDto;
import dgucomai.tableorder.dto.res.TableNumResDto;
import dgucomai.tableorder.dto.res.TableSummaryResDto;
import dgucomai.tableorder.exception.CustomException;
import dgucomai.tableorder.exception.ErrorCode;
import dgucomai.tableorder.repository.OrdersRepository;
import dgucomai.tableorder.repository.StaffCallRepository;
import dgucomai.tableorder.repository.table.StaffRepository;
import dgucomai.tableorder.repository.table.TableRepository;
import dgucomai.tableorder.repository.table.TableSessionRepository;
import dgucomai.tableorder.sse.SseEmitterManager;
import jakarta.persistence.EntityManager;
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
  private final SseEmitterManager sseEmitterManager;
  private final EntityManager em;

  @Transactional(readOnly = true)
  public TableNumResDto getTableNumByQrToken(String qrToken) {
    Tables table =
        tableRepository
            .findByQrToken(qrToken)
            .orElseThrow(
                () ->
                    new CustomException(ErrorCode.TABLE_NOT_FOUND, "QR 토큰에 해당하는 테이블을 찾을 수 없습니다."));
    return TableNumResDto.from(table);
  }

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

                  if (staffCallRepository.existsBySessionIdAndCallTypeAndStatus(
                      session.getSessionId(), "DEALER", "REQUESTED")) {
                    calculatedStatus = TableStatus.DEALER_CALL;
                  } else if (staffCallRepository.existsBySessionIdAndCallTypeAndStatus(
                      session.getSessionId(), "STAFF", "REQUESTED")) {
                    calculatedStatus = TableStatus.STAFF_CALL;
                  } else if (hasPaymentPending(session.getSessionId())) {
                    calculatedStatus = TableStatus.PAYMENT_PENDING;
                  }
                }
              }

              return TableSummaryResDto.from(table, calculatedStatus);
            })
        .collect(Collectors.toList());
  }

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

    if (session.getStatus() == TableSessionStatus.CLOSED) {
      return TableDetailResDto.empty(table, session.getSessionId());
    }

    List<Orders> dbOrders = ordersRepository.findBySessionId(session.getSessionId());
    List<StaffCall> dbCalls = staffCallRepository.findBySessionId(session.getSessionId());

    TableStatus calculatedStatus = TableStatus.IN_USE;

    boolean hasDealerCall =
        dbCalls.stream()
            .anyMatch(c -> "DEALER".equals(c.getCallType()) && "REQUESTED".equals(c.getStatus()));
    boolean hasStaffCall =
        dbCalls.stream()
            .anyMatch(c -> "STAFF".equals(c.getCallType()) && "REQUESTED".equals(c.getStatus()));

    boolean hasPaymentPending = hasPaymentPending(session.getSessionId());

    if (hasDealerCall) {
      calculatedStatus = TableStatus.DEALER_CALL;
    } else if (hasStaffCall) {
      calculatedStatus = TableStatus.STAFF_CALL;
    } else if (hasPaymentPending) {
      calculatedStatus = TableStatus.PAYMENT_PENDING;
    }

    int amount =
        dbOrders.stream()
            .filter(o -> o.getOrderStatus() != OrderStatus.REJECTED)
            .mapToInt(Orders::getAmount)
            .sum();

    List<OrderResDto> orderDtos =
        dbOrders.stream().map(OrderResDto::from).collect(Collectors.toList());

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

    return TableDetailResDto.of(table, session, calculatedStatus, amount, orderDtos, callDtos);
  }

  @Transactional
  public TableDetailResDto clearTable(Long tableId, Long staffId) {
    Tables table =
        tableRepository
            .findById(tableId)
            .orElseThrow(() -> new EntityNotFoundException("테이블을 찾을 수 없습니다. ID: " + tableId));

    if (table.getCurrentSessionId() == null) {
      return TableDetailResDto.empty(table, null);
    }

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
            .startedAt(null)
            .build();
    TableSession savedNewSession = tableSessionRepository.save(newSession);

    table.setCurrentSessionId(savedNewSession.getSessionId());
    tableRepository.save(table);

    sseEmitterManager.sendEventToStaff("TABLE_STATUS_CHANGED", tableId);
    return TableDetailResDto.empty(table, savedNewSession.getSessionId());
  }

  private boolean hasPaymentPending(Long sessionId) {
    String jpql =
        "SELECT COUNT(pr) FROM PaymentRequest pr "
            + "JOIN pr.order o "
            + "WHERE o.sessionId = :sessionId AND pr.paymentStatus = :status";

    Long count =
        em.createQuery(jpql, Long.class)
            .setParameter("sessionId", sessionId)
            .setParameter("status", PaymentStatus.PENDING)
            .getSingleResult();

    return count > 0;
  }
}
