-- 순서대로 삭제
#DROP TABLE com_chat_room;
#DROP TABLE com_message;
#DROP TABLE com_chat_participate;
#DROP TABLE member;

-- 테스트 임시 멤버 테이블 - 병합 시 삭제 예정
CREATE TABLE member
(
    num      BIGINT,
    id       VARCHAR(12),
    password VARCHAR(60), -- 암호화를 하면 password가 60자 필요합니다.
    name     VARCHAR(15)
#    auth   		varchar(50) not null, -- 회원의 권한을 저장하는 곳으로 기본값은 'ROLE_MEMBER' 입니다.
);

ALTER TABLE member
    ADD CONSTRAINT PK_MEMBER_NUM PRIMARY KEY (num);

-- 채팅방
CREATE TABLE com_chat_room
(
    chat_room_num    BIGINT                             NOT NULL,
    chat_create_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    name             VARCHAR(50)                        NOT NULL
);

-- "chat_room"."chat_room_num"    : '채팅방 번호';
-- "chat_room"."ch_create_date"   : '채팅방 생성일';
-- "chat_room"."name"             : '채팅방 이름';

-- com_chat_room-PK
ALTER TABLE com_chat_room
    ADD CONSTRAINT PK_CHATROOM_NUM PRIMARY KEY (chat_room_num);

-- 메시지
CREATE TABLE com_message
(
    message_num     BIGINT                             NOT NULL,
    message_content VARCHAR(300)                       NOT NULL,
    read_count      INT(11)                            NOT NULL,
    send_time       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    sender_num      BIGINT                             NOT NULL,
    chat_room_num   BIGINT                             NOT NULL
);

-- "message"."message_no"      : '메시지 번호';
-- "message"."message_content" : '메시지 내용';
-- "message"."read_fl"         : '안 읽은 사람 수';
-- "message"."send_time"       : '메시지 보낸 시간';
-- "message"."sender_num"       : '보낸 회원의 번호';
-- "message"."chatroom_num"     : '채팅방 번호';

-- message-PK
ALTER TABLE com_message
    ADD CONSTRAINT PK_MESSAGE_NUM PRIMARY KEY (message_num);


-- message-FK
ALTER TABLE com_message
    ADD CONSTRAINT FK_CHAT_MESSAGE_NUM
        FOREIGN KEY (chat_room_num) REFERENCES com_chat_room (chat_room_num)
;

ALTER TABLE com_message
    ADD CONSTRAINT FK_SENDER_MESSAGE_NUM
        FOREIGN KEY (sender_num) REFERENCES member (num);

-- 채팅 참여
CREATE TABLE com_chat_participate
(
    sender_num    BIGINT      NOT NULL,
    chat_room_num BIGINT      NOT NULL,
    chat_auth     VARCHAR(10) NOT NULL
);

-- "com_chat_participate"."sender_num"   : '보낸 회원의 번호';
-- "com_chat_participate"."chatroom_num" : '채팅방 번호';
-- "com_chat_participate"."chat_auth"    : '채팅방 권한';

--  com_chat_participate-FK
ALTER TABLE com_chat_participate
    ADD CONSTRAINT FK_CHAT_PART_NUM
        FOREIGN KEY (chat_room_num) REFERENCES com_chat_room (chat_room_num);

ALTER TABLE com_chat_participate
    ADD CONSTRAINT FK_SENDER_PART_NUM
        FOREIGN KEY (sender_num) REFERENCES member (num);

-- 시퀀스 대신 사용
#<selectKey resultType="int" order="BEFORE" keyProperty="chatroom_num">
#    select nvl(max(chatroom_num),0)+1 from com_chat_room
#</selectKey>