package kr.co.escape.api.scheduler;

import kr.co.escape.api.service.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 알림 스케줄러
 * 1분마다 테스트 메시지를 텔레그램으로 전송합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertScheduler {

    private final TelegramService telegramService;

    /**
     * 1분마다 실행되는 테스트 알림
     * cron: 초 분 시 일 월 요일
     * "0 * * * * *" = 매 분 0초에 실행
     */
    //@Scheduled(cron = "0 * * * * *")
    public void sendTestAlert() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String message = String.format("alert-test [%s]", timestamp);

        log.info("스케줄러 실행: {}", message);
        telegramService.sendTestMessage(message);
    }

    // TODO: 향후 실제 알림 로직 구현
    // @Scheduled(fixedDelay = 300000) // 5분마다
    // public void checkAvailabilityAndNotify() {
    //     log.info("예약 가능 여부 확인 시작");
    //     // 1. Availability 데이터 크롤링 또는 조회
    //     // 2. UserAlert 조건과 매칭
    //     // 3. 조건 일치 시 알림 전송
    //     // 4. Notification 엔티티에 이력 저장
    // }
}
