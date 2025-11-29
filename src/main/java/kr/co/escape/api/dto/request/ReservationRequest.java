package kr.co.escape.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "예약 요청 정보")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    @Schema(description = "테마 ID - 주중(월~목): 51:ALIVE, 54:사랑하는감, 56:깜방탈출, 58:NOX, 50:층간소음 / 주말(금~일): 52:ALIVE, 55:사랑하는감, 57:깜방탈출, 59:NOX, 51:층간소음", example = "57")
    private String themeId;

    @Schema(description = "예약 날짜 (YYYY-MM-DD)", example = "2025-12-13")
    private String reservationDate;

    @Schema(description = "예약 시간 (HH:mm:ss)", example = "17:00:00")
    private String reservationTime;

    @Schema(description = "예약자 이름", example = "홍길동")
    private String name;

    @Schema(description = "연락처 (하이픈 포함)", example = "010-1234-5678")
    private String phone;

    @Schema(description = "인원 수", example = "2")
    private Integer people;

    @Schema(description = "결제 방식 (1: 현장결제)", example = "1")
    private String paymentType;

    @Schema(description = "개인정보 처리방침 동의", example = "true")
    private boolean policy;
}
