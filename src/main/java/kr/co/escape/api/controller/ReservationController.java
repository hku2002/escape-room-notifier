package kr.co.escape.api.controller;

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

@Slf4j
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ApiResponse<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
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
