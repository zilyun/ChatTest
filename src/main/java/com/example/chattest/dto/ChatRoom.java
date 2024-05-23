package com.example.chattest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ChatRoom {
    private long chat_room_num;
    private String chat_room_id;
    private Date chat_room_date;
    private String name;
    private Set<WebSocketSession> sessions = new HashSet<>();

    @Builder
    public ChatRoom(long chat_room_num, String chat_room_id, Date chat_room_date, String name) {
        this.chat_room_num = chat_room_num;
        this.chat_room_date = chat_room_date;
        this.chat_room_id = chat_room_id;
        this.name = name;
    }
}