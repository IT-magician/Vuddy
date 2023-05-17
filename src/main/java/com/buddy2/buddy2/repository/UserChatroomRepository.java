package com.buddy2.buddy2.repository;

import com.buddy2.buddy2.entity.UserChatroom;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserChatroomRepository extends JpaRepository<UserChatroom, Long> {

    @Query(value = "SELECT uc.chat_id FROM user_chatroom uc INNER JOIN user_chatroom uc2 ON uc.chat_id = uc2.chat_id WHERE (uc.user_id = :userId1 AND uc2.user_id = :userId2) OR (uc.user_id = :userId2 AND uc2.user_id = :userId1)", nativeQuery = true)
    Long findChatroomId(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
