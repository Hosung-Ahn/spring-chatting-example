package com.example.chat.websocket;

import com.example.chat.domain.chat.ChatRoom;
import com.example.chat.dto.websocket.ChatMessageReceiveDto;
import com.example.chat.repository.ChatRoomRepository;
import com.example.chat.service.RabbitMQPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebsocketController {
    private final RabbitMQPublishService rabbitMQPublishService;
    private final ChatRoomRepository chatRoomRepository;

    @MessageMapping("/chat/message/{chatRoomId}")
    public void send(ChatMessageReceiveDto chatDto, @DestinationVariable String chatRoomId) {
        ChatRoom.Message message = new ChatRoom.Message(chatDto);
        chatRoomRepository.appendMessage(chatRoomId, message);
        rabbitMQPublishService.publishChatMessage(chatRoomId, message);
    }
}
