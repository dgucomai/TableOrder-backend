package dgucomai.tableorder.sse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {
  private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

  public SseEmitter connect(Long tableNo) {
    SseEmitter emitter = new SseEmitter(60 * 1000L * 30);
    emitters.put(tableNo, emitter);
    emitter.onCompletion(() -> emitters.remove(tableNo));
    emitter.onTimeout(() -> emitters.remove(tableNo));
    return emitter;
  }

  public void sendStaffCallEvent(Long tableId, String message, String status) {
    emitters.forEach(
        (id, emitter) -> {
          try {
            emitter.send(
                SseEmitter.event()
                    .name("STAFF_CALL_CREATED")
                    .data(
                        Map.of(
                            "tableId", tableId,
                            "tableNumber", tableId,
                            "message", message,
                            "status", status,
                            "createdAt", LocalDateTime.now().toString())));
          } catch (IOException e) {
            emitters.remove(id);
          }
        });
  }
}
