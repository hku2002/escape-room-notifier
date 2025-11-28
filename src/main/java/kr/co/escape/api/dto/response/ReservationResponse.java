package kr.co.escape.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private boolean success;
    private String message;
    private String reservationId;

    public static ReservationResponse success(String message, String reservationId) {
        return new ReservationResponse(true, message, reservationId);
    }

    public static ReservationResponse failure(String message) {
        return new ReservationResponse(false, message, null);
    }
}
