package com.edu.ssafy.friend.model.service;

import com.edu.ssafy.friend.model.entity.User;
import com.edu.ssafy.friend.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

}