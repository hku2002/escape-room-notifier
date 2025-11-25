package kr.co.escape.api.controller;

import kr.co.escape.api.dto.response.ApiResponse;
import kr.co.escape.api.dto.response.UserAlertResponse;
import kr.co.escape.api.service.UserAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user-alerts")
@RequiredArgsConstructor
public class UserAlertController {

    private final UserAlertService userAlertService;

    @GetMapping
    public ApiResponse<List<UserAlertResponse>> getUserAlerts(
            @RequestParam(required = false) Long userId
    ) {
        List<UserAlertResponse> userAlerts;

        if (userId != null) {
            userAlerts = userAlertService.getUserAlertsByUserId(userId);
        } else {
            userAlerts = userAlertService.getAllUserAlerts();
        }

        return ApiResponse.success(userAlerts);
    }
}
