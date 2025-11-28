package kr.co.escape.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    private String themeId;
    private String reservationDate;
    private String timeSlot;
    private String name;
    private String phone;
    private Integer people;
    private String paymentType;
    private boolean policy;
}
