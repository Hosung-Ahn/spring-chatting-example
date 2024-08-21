package com.example.chat.websocket;

import com.example.chat.domain.chat.JoinedChatRoom;
import com.example.chat.repository.ChatSessionRepository;
import com.example.chat.repository.JoinedChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebsocketEventListener implements ChannelInterceptor {
    private final ChatSessionRepository chatSessionRepository;
    private final JoinedChatRoomRepository joinedChatRoomRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String type = headerAccessor.getFirstNativeHeader("type");
        log.info("WebSocket Connection Type: " + type + "/ SessionId: " + headerAccessor.getSessionId());

        if (WebSocketConnectionType.valueOf(type) == WebSocketConnectionType.CHAT_ROOM) {
            String sessionId = headerAccessor.getSessionId();
            String memberId = headerAccessor.getFirstNativeHeader("memberId");
            String chatRoomId = headerAccessor.getFirstNativeHeader("chatRoomId");
            chatSessionRepository.saveSessionData(sessionId, memberId, chatRoomId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectionListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        log.info("WebSocket Disconnection SessionId: " + sessionId);

        if (chatSessionRepository.isSessionDataExist(sessionId)) {
            Long memberId = Long.getLong(chatSessionRepository.getMemberId(sessionId));
            String chatRoomId = chatSessionRepository.getChatRoomId(sessionId);
            chatSessionRepository.deleteSessionData(sessionId);
            JoinedChatRoom joinedChatRoom = joinedChatRoomRepository.findById(chatRoomId, memberId);
            joinedChatRoom.updateLastAccessTime();
        }
    }
}
