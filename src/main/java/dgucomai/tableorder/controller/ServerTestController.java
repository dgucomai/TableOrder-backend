package dgucomai.tableorder.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class ServerTestController {

    @GetMapping("/api/test")
    public String getServerTime() {
        LocalDateTime now = LocalDateTime.now();
        return "현재 서버 시간 : " + now;
    }

}
