package com.lzk.user.service;

import com.lzk.user.dto.LoginResponse;
import com.lzk.user.entity.User;
import com.lzk.user.mapper.UserMapper;
import jakarta.security.auth.message.AuthException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void testLoginSuccess() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("123456");
        
        when(userMapper.selectOne(any())).thenReturn(mockUser);
        
        LoginResponse response = userService.login("testuser", "123456");
        
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getUser());
        assertEquals("testuser", response.getUser().getUsername());
        assertNull(response.getUser().getPassword());
    }

    @Test
    void testLoginFailure() {
        when(userMapper.selectOne(any())).thenReturn(null);

        assertThrows(AuthException.class, () -> {
            userService.login("wronguser", "wrongpass");
        });
    }
}
