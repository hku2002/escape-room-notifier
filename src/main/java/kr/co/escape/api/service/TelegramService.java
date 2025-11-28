package kr.co.escape.api.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 텔레그램 메시지 전송 서비스
 */
@Slf4j
@Service
public class TelegramService {

    private final TelegramBot bot;
    private final String testChatId;

    public TelegramService(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.test-chat-id}") String testChatId
    ) {
        this.bot = new TelegramBot(botToken);
        this.testChatId = testChatId;
        log.info("TelegramService initialized with chat ID: {}", testChatId);
    }

    /**
     * 테스트용 Chat ID로 메시지 전송
     *
     * @param message 전송할 메시지
     */
    public void sendTestMessage(String message) {
        sendMessage(testChatId, message);
    }

    /**
     * 특정 Chat ID로 메시지 전송
     *
     * @param chatId  수신자 Chat ID
     * @param message 전송할 메시지
     */
    public void sendMessage(String chatId, String message) {
        try {
            SendMessage request = new SendMessage(chatId, message);
            SendResponse response = bot.execute(request);

            if (response.isOk()) {
                log.info("메시지 전송 성공: chatId={}, message={}", chatId, message);
            } else {
                log.error("메시지 전송 실패: errorCode={}, description={}",
                        response.errorCode(), response.description());
            }
        } catch (Exception e) {
            log.error("텔레그램 메시지 전송 중 오류 발생: chatId={}, message={}", chatId, message, e);
        }
    }

    /**
     * 사용자 ID로 메시지 전송 (향후 구현)
     *
     * @param userId  사용자 ID
     * @param message 전송할 메시지
     */
    public void sendMessageToUser(Long userId, String message) {
        // TODO: User 엔티티에서 telegramChatId를 조회하여 메시지 전송
        log.info("sendMessageToUser 호출: userId={}, message={}", userId, message);
    }
}
