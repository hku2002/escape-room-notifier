package kr.co.escape.api.config;

import kr.co.escape.api.entity.*;
import kr.co.escape.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final UserAlertRepository userAlertRepository;
    private final AvailabilityRepository availabilityRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("테스트 데이터 초기화 시작...");

        // 1. 사용자 생성
        User user1 = new User("test1@example.com", "password123", "email,kakao");
        User user2 = new User("test2@example.com", "password456", "email");
        userRepository.save(user1);
        userRepository.save(user2);

        // 2. 테마 생성
        Theme theme1 = new Theme(
                "넥스트에디션",
                "더 지니어스",
                "강남점",
                "상",
                "추리",
                90,
                30000
        );

        Theme theme2 = new Theme(
                "비트포비아",
                "살인마의 방",
                "홍대점",
                "중상",
                "공포",
                75,
                28000
        );

        themeRepository.save(theme1);
        themeRepository.save(theme2);

        // 3. 사용자 알림 설정 생성
        UserAlert alert1 = new UserAlert(
                user1,
                theme1,
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 31),
                "[\"18:00\", \"19:00\", \"20:00\"]",
                4
        );

        UserAlert alert2 = new UserAlert(
                user1,
                theme2,
                LocalDate.of(2025, 11, 20),
                LocalDate.of(2025, 11, 30),
                "[\"14:00\", \"15:00\", \"16:00\"]",
                3
        );
        alert2.deactivate(); // 비활성화 상태

        userAlertRepository.save(alert1);
        userAlertRepository.save(alert2);

        // 4. 예약 가능 정보 생성
        Availability availability1 = new Availability(
                theme1,
                LocalDate.of(2025, 12, 15),
                LocalTime.of(18, 0),
                true
        );

        Availability availability2 = new Availability(
                theme2,
                LocalDate.of(2025, 11, 25),
                LocalTime.of(14, 0),
                true
        );

        availabilityRepository.save(availability1);
        availabilityRepository.save(availability2);

        // 5. 알림 히스토리 생성
        Notification notification1 = new Notification(
                user1,
                alert1,
                availability1,
                "email",
                "success"
        );

        Notification notification2 = new Notification(
                user1,
                alert1,
                availability1,
                "kakao",
                "success"
        );

        Notification notification3 = new Notification(
                user1,
                alert1,
                availability1,
                "email",
                "success"
        );

        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        notificationRepository.save(notification3);

        log.info("테스트 데이터 초기화 완료!");
        log.info("- 사용자 2명");
        log.info("- 테마 2개");
        log.info("- 알림 설정 2개 (활성 1개, 비활성 1개)");
        log.info("- 예약 가능 정보 2개");
        log.info("- 알림 히스토리 3개");
    }
}
