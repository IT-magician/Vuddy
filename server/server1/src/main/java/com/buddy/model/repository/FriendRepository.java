package com.buddy.model.repository;

import com.buddy.model.entity.User;
import com.buddy.model.entity.UserFriends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<UserFriends, Long> {

    boolean existsByRequestUserAndReceiveUser(User requester, User receiver);

}
