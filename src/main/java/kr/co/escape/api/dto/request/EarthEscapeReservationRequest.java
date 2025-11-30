package kr.co.escape.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "지구별 방탈출 예약 요청 정보")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EarthEscapeReservationRequest {

    @Schema(description = "지점 ID (2: 홍대어드벤처점)", example = "2")
    private String branch;

    @Schema(description = "테마 ID (18: 아몬-새벽을 여는 소년, 등)", example = "18")
    private String theme;

    @Schema(description = "예약 날짜 (YYYY-MM-DD)", example = "2025-12-04")
    private String date;

    @Schema(description = "예약 시간 (HH:mm)", example = "20:10")
    private String time;

    @Schema(description = "예약자 이름", example = "홍길동")
    private String name;

    @Schema(description = "연락처 (하이픈 포함)", example = "010-1234-5678")
    private String phone;

    @Schema(description = "인원 수 (2-6명)", example = "4")
    private String people;

    @Schema(description = "결제 방식 (21: 가상계좌)", example = "21")
    private String paymentMethod;

    @Schema(description = "개인정보 처리방침 동의 (체크박스)", example = "true")
    private boolean policy;
}
