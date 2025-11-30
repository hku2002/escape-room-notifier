package kr.co.escape.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.escape.api.dto.request.EarthEscapeReservationRequest;
import kr.co.escape.api.dto.request.ReservationRequest;
import kr.co.escape.api.dto.response.ReservationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private static final String ZERO_WORLD_BASE_URL = "https://zerohongdae.com";
    private static final String RESERVATION_PATH = "/reservation";
    private static final String EARTH_ESCAPE_BASE_URL = "https://www.xn--2e0b040a4xj.com";
    private static final String EARTH_ESCAPE_RESERVATION_PATH = "/reservation";

    public ReservationResponse makeReservation(ReservationRequest request) {
        try {
            // 1단계: 예약 페이지 접근하여 CSRF 토큰과 쿠키 획득
            String pageUrl = ZERO_WORLD_BASE_URL + RESERVATION_PATH + "/" + request.getThemeId();
            String reservationUrl = ZERO_WORLD_BASE_URL + RESERVATION_PATH;

            // GET 요청으로 페이지 조회
            ResponseData responseData = webClient.get()
                    .uri(pageUrl)
                    .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .exchangeToMono(response -> {
                        // 쿠키 추출
                        List<String> cookies = response.headers().asHttpHeaders().get(HttpHeaders.SET_COOKIE);
                        String cookieHeader = buildCookieHeader(cookies);

                        return response.bodyToMono(String.class)
                                .map(body -> new PageData(body, cookieHeader));
                    })
                    .map(pageData -> {
                        // CSRF 토큰 추출
                        String csrfToken = extractCsrfToken(pageData.html);
                        if (csrfToken == null) {
                            throw new RuntimeException("CSRF 토큰을 찾을 수 없습니다.");
                        }
                        return new PageData(pageData.html, pageData.cookieHeader, csrfToken);
                    })
                    .flatMap(pageData -> {
                        // 2단계: 예약 요청 전송
                        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                        formData.add("themePK", request.getThemeId());
                        formData.add("name", request.getName());
                        formData.add("phone", request.getPhone());
                        formData.add("people", String.valueOf(request.getPeople()));
                        formData.add("paymentType", request.getPaymentType());
                        formData.add("policy", request.isPolicy() ? "on" : "");
                        formData.add("reservationDate", request.getReservationDate());
                        formData.add("reservationTime", request.getReservationTime());

                        log.info("Sending POST request to: {}", reservationUrl);
                        log.info("CSRF Token: {}", pageData.csrfToken);
                        log.info("Cookie: {}", pageData.cookieHeader);
                        log.info("Form Data: {}", formData);

                        return webClient.post()
                                .uri(reservationUrl)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                                .header(HttpHeaders.ACCEPT, "application/json, text/javascript, */*; q=0.01")
                                .header("X-CSRF-TOKEN", pageData.csrfToken)
                                .header("X-Requested-With", "XMLHttpRequest")
                                .header(HttpHeaders.REFERER, reservationUrl)
                                .header(HttpHeaders.ORIGIN, ZERO_WORLD_BASE_URL)
                                .header(HttpHeaders.COOKIE, pageData.cookieHeader != null ? pageData.cookieHeader : "")
                                .body(BodyInserters.fromFormData(formData))
                                .exchangeToMono(response -> {
                                    log.info("Response status: {}", response.statusCode());
                                    log.info("Response headers: {}", response.headers().asHttpHeaders());

                                    int statusCode = response.statusCode().value();

                                    return response.bodyToMono(String.class)
                                            .doOnNext(body -> log.info("Response body: {}", body))
                                            .map(body -> new ResponseData(statusCode, body));
                                });
                    })
                    .block();

            // 응답이 없거나 HTTP 상태 코드가 200이 아니면 실패 처리
            if (responseData == null) {
                log.error("No response received from server");
                return ReservationResponse.failure("서버로부터 응답을 받지 못했습니다.");
            }

            if (responseData.statusCode != 200) {
                log.warn("Reservation failed with status code: {}, body: {}", responseData.statusCode, responseData.body);
                String errorMessage = extractErrorMessage(responseData.body);
                return ReservationResponse.failure(errorMessage);
            }

            log.info("Reservation successful: {}", responseData.body);
            return ReservationResponse.success("예약이 완료되었습니다.", extractReservationId(responseData.body));

        } catch (Exception e) {
            log.error("Error making reservation", e);
            return ReservationResponse.failure("예약 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private static class PageData {
        String html;
        String cookieHeader;
        String csrfToken;

        PageData(String html, String cookieHeader) {
            this.html = html;
            this.cookieHeader = cookieHeader;
        }

        PageData(String html, String cookieHeader, String csrfToken) {
            this.html = html;
            this.cookieHeader = cookieHeader;
            this.csrfToken = csrfToken;
        }
    }

    private static class ResponseData {
        int statusCode;
        String body;

        ResponseData(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }
    }

    private String extractCsrfToken(String html) {
        if (html == null) {
            return null;
        }

        // <meta name="csrf-token" id="csrf" content="TOKEN_VALUE">
        Pattern pattern = Pattern.compile("<meta name=\"csrf-token\"[^>]*content=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String buildCookieHeader(List<String> cookies) {
        if (cookies == null || cookies.isEmpty()) {
            return null;
        }

        StringBuilder cookieBuilder = new StringBuilder();
        for (String cookie : cookies) {
            String cookieName = cookie.split(";")[0];
            if (cookieBuilder.length() > 0) {
                cookieBuilder.append("; ");
            }
            cookieBuilder.append(cookieName);
        }

        return cookieBuilder.toString();
    }

    private String extractReservationId(String response) {
        // 응답에서 예약 ID를 추출하는 로직
        // 실제 응답 형식에 따라 수정 필요
        return null;
    }

    /**
     * JSON 응답에서 에러 메시지 추출
     * @param jsonBody JSON 형태의 응답 body
     * @return 사람이 읽을 수 있는 에러 메시지
     */
    private String extractErrorMessage(String jsonBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonBody);
            if (jsonNode.has("message")) {
                return jsonNode.get("message").asText();
            }
            return "예약 실패: " + jsonBody;
        } catch (Exception e) {
            log.warn("Failed to parse error message from JSON: {}", jsonBody, e);
            return "예약 실패: " + jsonBody;
        }
    }

    /**
     * Unix 타임스탬프를 H:i:s 형식(HH:mm:ss)으로 변환
     * @param timestamp Unix 타임스탬프 문자열
     * @return HH:mm:ss 형식의 시간 문자열
     */
    private String convertTimestampToTime(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            throw new IllegalArgumentException("타임스탬프가 비어있습니다.");
        }

        try {
            long epochSecond = Long.parseLong(timestamp);
            Instant instant = Instant.ofEpochSecond(epochSecond);
            ZonedDateTime zdt = instant.atZone(ZoneId.of("Asia/Seoul"));
            return zdt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("유효하지 않은 타임스탬프 형식입니다: " + timestamp, e);
        }
    }

    /**
     * 지구별 방탈출 예약 요청
     * @param request 지구별 방탈출 예약 요청 정보
     * @return 예약 결과
     */
    public ReservationResponse makeEarthEscapeReservation(EarthEscapeReservationRequest request) {
        try {
            // 1단계: /reservation 페이지에 접근하여 CSRF 토큰과 쿠키 획득
            String reservationListUrl = EARTH_ESCAPE_BASE_URL + "/reservation";
            String reservationCreateUrl = EARTH_ESCAPE_BASE_URL + EARTH_ESCAPE_RESERVATION_PATH;

            log.info("Accessing Earth Escape reservation list page: {}", reservationListUrl);

            // GET 요청으로 예약 목록 페이지 조회 (CSRF 토큰 획득)
            ResponseData responseData = webClient.get()
                    .uri(reservationListUrl)
                    .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .exchangeToMono(response -> {
                        // 쿠키 추출
                        List<String> cookies = response.headers().asHttpHeaders().get(HttpHeaders.SET_COOKIE);
                        String cookieHeader = buildCookieHeader(cookies);

                        return response.bodyToMono(String.class)
                                .map(body -> new PageData(body, cookieHeader));
                    })
                    .map(pageData -> {
                        // CSRF 토큰 추출
                        String csrfToken = extractCsrfToken(pageData.html);
                        if (csrfToken == null) {
                            throw new RuntimeException("CSRF 토큰을 찾을 수 없습니다.");
                        }
                        log.info("Successfully extracted CSRF token");
                        return new PageData(pageData.html, pageData.cookieHeader, csrfToken);
                    })
                    .flatMap(pageData -> {
                        // 2단계: 예약 요청 전송
                        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                        formData.add("branch", request.getBranch());
                        formData.add("theme", request.getTheme());
                        formData.add("date", request.getDate());
                        formData.add("time", request.getTime());
                        formData.add("name", request.getName());
                        formData.add("phone", request.getPhone());
                        formData.add("people", request.getPeople());
                        formData.add("payment_method", request.getPaymentMethod());
                        formData.add("policy", request.isPolicy() ? "on" : "");
                        formData.add("_token", pageData.csrfToken);  // Laravel CSRF 토큰 필드

                        log.info("Sending POST request to: {}", reservationCreateUrl);
                        log.info("CSRF Token: {}", pageData.csrfToken);
                        log.info("Cookie: {}", pageData.cookieHeader);
                        log.info("Form Data: {}", formData);

                        return webClient.post()
                                .uri(reservationCreateUrl)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                                .header(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                                .header(HttpHeaders.REFERER, reservationListUrl)
                                .header(HttpHeaders.ORIGIN, EARTH_ESCAPE_BASE_URL)
                                .header(HttpHeaders.COOKIE, pageData.cookieHeader != null ? pageData.cookieHeader : "")
                                .body(BodyInserters.fromFormData(formData))
                                .exchangeToMono(response -> {
                                    log.info("Response status: {}", response.statusCode());
                                    log.info("Response headers: {}", response.headers().asHttpHeaders());

                                    int statusCode = response.statusCode().value();

                                    return response.bodyToMono(String.class)
                                            .doOnNext(body -> log.info("Response body: {}", body))
                                            .map(body -> new ResponseData(statusCode, body));
                                });
                    })
                    .block();

            // 응답이 없거나 HTTP 상태 코드가 200/302가 아니면 실패 처리
            if (responseData == null) {
                log.error("No response received from server");
                return ReservationResponse.failure("서버로부터 응답을 받지 못했습니다.");
            }

            // 지구별 방탈출은 성공시 리다이렉트(302)를 반환할 수 있음
            if (responseData.statusCode != 200 && responseData.statusCode != 302) {
                log.warn("Reservation failed with status code: {}, body: {}", responseData.statusCode, responseData.body);
                String errorMessage = extractErrorMessage(responseData.body);
                return ReservationResponse.failure(errorMessage);
            }

            log.info("Earth Escape reservation successful: {}", responseData.body);
            return ReservationResponse.success("예약이 완료되었습니다.", extractReservationId(responseData.body));

        } catch (Exception e) {
            log.error("Error making Earth Escape reservation", e);
            return ReservationResponse.failure("예약 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
