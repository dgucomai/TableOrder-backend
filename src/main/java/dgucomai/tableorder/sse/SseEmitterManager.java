package dgucomai.tableorder.sse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmitterManager {

  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  public SseEmitter connect(String tableNo) {
    SseEmitter emitter = new SseEmitter(1800000L);
    emitters.put(tableNo, emitter);

    emitter.onCompletion(() -> emitters.remove(tableNo));
    emitter.onTimeout(() -> emitters.remove(tableNo));
    emitter.onError(e -> emitters.remove(tableNo));

    try {
      emitter.send(SseEmitter.event().name("connected").data("connected"));
    } catch (IOException e) {
      emitters.remove(tableNo);
      emitter.completeWithError(e);
    }

    return emitter;
  }

  public void broadcastSoldOut(Long menuId, boolean isSoldOut) {
    Map<String, Object> data = Map.of("menuId", menuId, "isSoldOut", isSoldOut);
    sendEventToAll("menu-sold-out", data);
  }

  public void sendEventToStaff(String eventName, Object data) {
    sendEventToAll(eventName, data);
  }

  private void sendEventToAll(String eventName, Object data) {
    for (Map.Entry<String, SseEmitter> entry : new ArrayList<>(emitters.entrySet())) {
      try {
        entry.getValue().send(SseEmitter.event().name(eventName).data(data));
      } catch (IOException e) {
        emitters.remove(entry.getKey());
      }
    }
  }
}
