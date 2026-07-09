package com.ohiggins.classflow.auth.service;

import com.ohiggins.classflow.auth.dto.*;
import com.ohiggins.classflow.auth.entity.Role;
import com.ohiggins.classflow.auth.entity.User;
import com.ohiggins.classflow.auth.exception.DuplicateResourceException;
import com.ohiggins.classflow.auth.exception.InvalidTokenException;
import com.ohiggins.classflow.auth.repository.UserRepository;
import com.ohiggins.classflow.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Contiene la logica de autenticacion, registro y cambio de contrasena.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    /**
     * Autentica credenciales y genera un token JWT.
     *
     * @param request credenciales de acceso.
     * @return respuesta con token y datos basicos del usuario.
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String fullName = user.getFirstName() + " " + user.getLastName();

        return new LoginResponseDTO(user.getId(), token, user.getEmail(), user.getRole().name(), fullName);
    }

    /**
     * Registra un nuevo usuario validando duplicados de email y documento.
     *
     * @param request datos del usuario a registrar.
     * @return usuario registrado convertido a DTO.
     */
    public UserResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("El correo electrónico '" + request.getEmail() + "' ya está registrado en el sistema.");
        }
        if (userRepository.existsByIdNumber(request.getIdNumber())) {
            throw new DuplicateResourceException("El RUT '" + request.getIdNumber() + "' ya existe en el sistema.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setIdNumber(request.getIdNumber());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        try {
            user.setRole(Role.valueOf(request.getRole()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol inválido: " + request.getRole());
        }
        user.setCourse(request.getCourse());
        user.setGuardianId(request.getGuardianId());

        User saved = userRepository.save(user);
        return userService.convertToDTO(saved);
    }

    /**
     * Valida un token JWT y retorna el usuario asociado.
     *
     * @param token token JWT a validar.
     * @return usuario asociado al token.
     */
    public UserResponseDTO validateToken(String token) {
        if (tokenProvider.validateToken(token)) {
            String email = tokenProvider.getEmailFromToken(token);
            return userService.findByEmail(email);
        }
        throw new InvalidTokenException("Invalid token");
    }

    /**
     * Cambia la contrasena del usuario identificado por email.
     *
     * @param email correo electronico del usuario.
     * @param request datos de cambio de contrasena.
     * @return usuario actualizado convertido a DTO.
     */
    public UserResponseDTO changePassword(String email, ChangePasswordRequestDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        User updated = userRepository.save(user);
        return userService.convertToDTO(updated);
    }
}