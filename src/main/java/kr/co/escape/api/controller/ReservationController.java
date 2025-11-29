package kr.co.escape.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.escape.api.dto.request.ReservationRequest;
import kr.co.escape.api.dto.response.ApiResponse;
import kr.co.escape.api.dto.response.ReservationResponse;
import kr.co.escape.api.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "예약 관리", description = "방탈출 카페 예약 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(
            summary = "제로월드 홍대점 예약",
            description = "제로월드 홍대점 방탈출 테마를 예약합니다. 테마 ID, 예약 날짜, 시간 등을 입력해야 합니다."
    )
    @PostMapping("/zero-world")
    public ApiResponse<ReservationResponse> createReservation(
            @Parameter(description = "예약 요청 정보", required = true)
            @RequestBody ReservationRequest request) {
        log.info("Reservation request received: themeId={}, date={}, name={}",
                request.getThemeId(), request.getReservationDate(), request.getName());

        ReservationResponse response = reservationService.makeReservation(request);

        if (response.isSuccess()) {
            return ApiResponse.success(response, "예약이 완료되었습니다.");
        } else {
            return ApiResponse.failure(response.getMessage());
        }
    }
}
