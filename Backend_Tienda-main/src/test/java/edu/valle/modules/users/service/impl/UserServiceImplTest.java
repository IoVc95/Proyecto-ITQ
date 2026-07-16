package edu.valle.modules.users.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.valle.common.enums.UserRole;
import edu.valle.exception.BusinessException;
import edu.valle.modules.users.dto.request.UserRequest;
import edu.valle.modules.users.dto.response.UserResponse;
import edu.valle.modules.users.entity.User;
import edu.valle.modules.users.mapper.UserMapper;
import edu.valle.modules.users.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder);
    }

    @Test
    void createEncodesPasswordBeforeSaving() {
        UserRequest request = request("new-password");
        User user = existingUser();
        UserResponse response = response();
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("new-password")).thenReturn("bcrypt-hash");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertSame(response, result);
        assertEquals("bcrypt-hash", user.getPasswordHash());
        verify(userRepository).save(user);
    }

    @Test
    void createRejectsMissingPassword() {
        assertThrows(BusinessException.class, () -> userService.create(request(null)));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createRejectsDuplicateEmail() {
        UserRequest request = request("new-password");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.create(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createRejectsDuplicateUsername() {
        UserRequest request = request("new-password");
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.create(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateKeepsCurrentHashWhenPasswordIsMissing() {
        UserRequest request = request(null);
        User user = existingUser();
        user.setPasswordHash("current-hash");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(request.username())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.update(1L, request);

        assertEquals("current-hash", user.getPasswordHash());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateEncodesNewPassword() {
        UserRequest request = request("replacement-password");
        User user = existingUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(request.username())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(request.password())).thenReturn("replacement-hash");
        when(userRepository.save(user)).thenReturn(user);

        userService.update(1L, request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("replacement-hash", captor.getValue().getPasswordHash());
    }

    private UserRequest request(String password) {
        return new UserRequest(
                "Test User",
                "test@example.com",
                "test-user",
                password,
                UserRole.ADMIN,
                true
        );
    }

    private User existingUser() {
        User user = new User();
        user.setId(1L);
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setUsername("test-user");
        user.setRole(UserRole.ADMIN);
        user.setActive(true);
        return user;
    }

    private UserResponse response() {
        return new UserResponse(
                1L,
                "Test User",
                "test@example.com",
                "test-user",
                UserRole.ADMIN,
                true,
                null,
                null
        );
    }
}
