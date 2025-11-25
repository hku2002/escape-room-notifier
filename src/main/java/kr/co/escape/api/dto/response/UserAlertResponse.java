package kr.co.escape.api.dto.response;

import kr.co.escape.api.entity.UserAlert;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserAlertResponse {

    private Long alertId;
    private Boolean isActive;
    private ThemeResponse theme;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private List<String> preferredTimes;
    private List<String> preferredDays;
    private Integer numPeople;
    private Integer notifiedCount;
    private LocalDateTime lastNotifiedAt;

    public static UserAlertResponse from(UserAlert userAlert, Integer notifiedCount, LocalDateTime lastNotifiedAt) {
        return new UserAlertResponse(
                userAlert.getId(),
                userAlert.getIsActive(),
                ThemeResponse.from(userAlert.getTheme()),
                userAlert.getDateStart(),
                userAlert.getDateEnd(),
                parsePreferredTimes(userAlert.getPreferredTimes()),
                parsePreferredDays(userAlert.getPreferredTimes()),
                userAlert.getNumPeople(),
                notifiedCount,
                lastNotifiedAt
        );
    }

    private static List<String> parsePreferredTimes(String preferredTimes) {
        // JSON 문자열을 파싱하여 시간 리스트 반환
        // 예: "[\"18:00\", \"19:00\", \"20:00\"]" -> ["18:00", "19:00", "20:00"]
        if (preferredTimes == null || preferredTimes.isEmpty()) {
            return List.of();
        }
        // 간단한 파싱 (실제로는 JSON 라이브러리 사용 권장)
        return List.of(preferredTimes.replaceAll("[\\[\\]\"]", "").split(","));
    }

    private static List<String> parsePreferredDays(String preferredTimes) {
        // 실제로는 별도 필드로 저장하거나 preferredTimes에서 요일 정보를 파싱
        // 현재는 빈 리스트 반환 (향후 구현 필요)
        return List.of();
    }
}