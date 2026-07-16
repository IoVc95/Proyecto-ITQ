package edu.valle.modules.auth.service.impl;

import edu.valle.common.enums.UserRole;
import edu.valle.exception.BusinessException;
import edu.valle.modules.auth.dto.request.LoginRequest;
import edu.valle.modules.auth.dto.request.RegisterRequest;
import edu.valle.modules.auth.dto.response.AuthResponse;
import edu.valle.modules.auth.service.AuthService;
import edu.valle.modules.users.entity.User;
import edu.valle.modules.users.repository.UserRepository;
import edu.valle.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl  implements AuthService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("El nombre de usuario ya está registrado");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("El email ya está registrado");
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                token,
                "Bearer",
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getUsername(),
                savedUser.getRole().name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new BusinessException("El usuario está desactivado");
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new BusinessException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}
