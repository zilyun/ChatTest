package com.example.chattest.config;

import com.example.chattest.dto.ChatMessage;
import com.example.chattest.dto.ChatRoom;
import com.example.chattest.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.swing.*;
import java.io.IOException;
import java.net.http.WebSocket;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSockChatHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private Set<WebSocketSession> sessions;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        // sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if(session == null) {

        }
        String payload = message.getPayload();
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        ChatRoom room = chatService.findRoomById(chatMessage.getChat_room_id());
        sessions = room.getSessions();   //방에 있는 현재 사용자 한명이 WebsocketSession
        if (chatMessage.getType().equals(ChatMessage.MessageType.ENTER)) {
            //사용자가 방에 입장하면  Enter 메세지를 보내도록 해놓음.
            // 이건 새로운 사용자가 socket 연결한 것이랑은 다름.
            //socket연결은 이 메세지 보내기전에 이미 되어있는 상태
            sessions.add(session);
            chatMessage.setMessage(chatMessage.getNickname() + "님이 입장했습니다.");  //TALK일 경우 msg가 있을 거고, ENTER일 경우 메세지 없으니까 message set
            sendToEachSocket(sessions, new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        } else if (chatMessage.getType().equals(ChatMessage.MessageType.QUIT)) {
            sessions.remove(session);
            chatMessage.setMessage(chatMessage.getNickname() + "님이 퇴장했습니다..");
            sendToEachSocket(sessions, new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        } else {
            sendToEachSocket(sessions, message); //입장,퇴장 아닐 때는 클라이언트로부터 온 메세지 그대로 전달.
        }
    }

    private void sendToEachSocket(Set<WebSocketSession> sessions, TextMessage message) {
        sessions.parallelStream().forEach(roomSession -> {
            try {
                roomSession.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //javascript에서  session.close해서 연결 끊음. 그리고 이 메소드 실행.
        //2024-05-23, 해당 메소드에서 처리를 안해주면 강제 세션 종료 시 이슈 발생
        super.afterConnectionClosed(session, status);
        sessions.remove(session);
        log.info("연결 끊겼어!");
    }


}
