package dgucomai.tableorder.logs.service;

import dgucomai.tableorder.logs.entity.ServiceLog;
import dgucomai.tableorder.logs.entity.TechLog;
import dgucomai.tableorder.logs.repository.ServiceLogRepository;
import dgucomai.tableorder.logs.repository.TechLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

  private final ServiceLogRepository serviceLogRepository;
  private final TechLogRepository techLogRepository;

  @Async
  @Transactional
  public void saveServiceLog(String actorType, String message) {
    serviceLogRepository.save(new ServiceLog(actorType, message));
  }

  @Async
  @Transactional
  public void saveTechLog(
      String method,
      String uri,
      String requestBody,
      Integer responseStatus,
      Long durationMs,
      String ipAddress) {
    techLogRepository.save(
        new TechLog(method, uri, requestBody, responseStatus, durationMs, ipAddress));
  }

  @Transactional(readOnly = true)
  public List<ServiceLog> findAllServiceLogs() {
    return serviceLogRepository.findAllByOrderByCreatedAtDesc();
  }

  @Transactional(readOnly = true)
  public List<TechLog> findAllTechLogs() {
    return techLogRepository.findAllByOrderByCreatedAtDesc();
  }
}
