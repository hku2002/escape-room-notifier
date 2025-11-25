package kr.co.escape.api.service;

import kr.co.escape.api.dto.response.UserAlertResponse;
import kr.co.escape.api.entity.UserAlert;
import kr.co.escape.api.repository.NotificationRepository;
import kr.co.escape.api.repository.UserAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAlertService {

    private final UserAlertRepository userAlertRepository;
    private final NotificationRepository notificationRepository;

    public List<UserAlertResponse> getAllUserAlerts() {
        List<UserAlert> userAlerts = userAlertRepository.findAll();

        return userAlerts.stream()
                .map(userAlert -> {
                    // 각 알림에 대한 알림 발송 횟수 조회
                    Integer notifiedCount = notificationRepository.findByUserAlertId(userAlert.getId()).size();

                    // 마지막 알림 발송 시간 조회
                    LocalDateTime lastNotifiedAt = notificationRepository.findByUserAlertId(userAlert.getId())
                            .stream()
                            .map(notification -> notification.getSentAt())
                            .max(LocalDateTime::compareTo)
                            .orElse(null);

                    return UserAlertResponse.from(userAlert, notifiedCount, lastNotifiedAt);
                })
                .collect(Collectors.toList());
    }

    public List<UserAlertResponse> getUserAlertsByUserId(Long userId) {
        List<UserAlert> userAlerts = userAlertRepository.findAll()
                .stream()
                .filter(userAlert -> userAlert.getUser().getId().equals(userId))
                .collect(Collectors.toList());

        return userAlerts.stream()
                .map(userAlert -> {
                    Integer notifiedCount = notificationRepository.findByUserAlertId(userAlert.getId()).size();
                    LocalDateTime lastNotifiedAt = notificationRepository.findByUserAlertId(userAlert.getId())
                            .stream()
                            .map(notification -> notification.getSentAt())
                            .max(LocalDateTime::compareTo)
                            .orElse(null);

                    return UserAlertResponse.from(userAlert, notifiedCount, lastNotifiedAt);
                })
                .collect(Collectors.toList());
    }
}
