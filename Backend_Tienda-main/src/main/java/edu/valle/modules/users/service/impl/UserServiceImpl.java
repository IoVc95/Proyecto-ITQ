package edu.valle.modules.users.service.impl;

import edu.valle.exception.BusinessException;
import edu.valle.exception.ResourceNotFoundException;
import edu.valle.modules.users.dto.request.UserRequest;
import edu.valle.modules.users.dto.response.UserResponse;
import edu.valle.modules.users.entity.User;
import edu.valle.modules.users.mapper.UserMapper;
import edu.valle.modules.users.repository.UserRepository;
import edu.valle.modules.users.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new BusinessException("Password is required");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("User email already exists");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username already exists");
        }
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        if (user.getActive() == null) {
            user.setActive(true);
        }
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return userMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = findEntityById(id);
        userRepository.findByEmail(request.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("User email already exists");
                });
        userRepository.findByUsername(request.username())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Username already exists");
                });
        userMapper.updateEntity(request, user);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        User user = findEntityById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    private User findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
