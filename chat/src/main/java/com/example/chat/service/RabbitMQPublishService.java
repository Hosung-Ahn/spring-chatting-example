package com.example.chat.service;

import com.example.chat.domain.chat.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQPublishService {
    private final RabbitTemplate rabbitTemplate;
    private final String EXCHANGE = "amq.topic"; // topic exchange 전략 사용

    /**
     * topic = 채팅방 ID
     * @param topic
     * @param message
     */
    public void publishChatMessage(String topic, ChatRoom.Message message) {
        rabbitTemplate.convertAndSend(EXCHANGE, topic, message);
    }
}
