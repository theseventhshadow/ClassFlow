package com.ohiggins.classflow.auth.controller;

import com.ohiggins.classflow.auth.dto.*;
import com.ohiggins.classflow.auth.service.AuthService;
import com.ohiggins.classflow.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Expone los endpoints REST para autenticacion y gestion de usuarios.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login, registro, validación de token y gestión de usuarios")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * Autentica un usuario y devuelve un token JWT.
     *
     * @param request credenciales de inicio de sesion.
     * @return respuesta HTTP con el token generado.
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request datos de registro del usuario.
     * @return respuesta HTTP con el usuario creado.
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "409", description = "Email o RUT ya registrado")
    })
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    /**
     * Valida un token JWT y retorna los datos del usuario.
     *
     * @param token encabezado Authorization con el token.
     * @return respuesta HTTP con el usuario autenticado.
     */
    @GetMapping("/validate")
    @Operation(summary = "Validar token JWT", description = "Verifica si el token es válido y retorna los datos del usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token válido"),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    public ResponseEntity<UserResponseDTO> validate(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(authService.validateToken(token));
    }

    /**
     * Obtiene un usuario por su identificador.
     *
     * @param id identificador del usuario.
     * @return respuesta HTTP con el usuario encontrado.
     */
    @GetMapping("/users/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * Obtiene un usuario por su correo electronico.
     *
     * @param email correo electronico del usuario.
     * @return respuesta HTTP con el usuario encontrado.
     */
    @GetMapping("/users/email/{email}")
    @Operation(summary = "Obtener usuario por email")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    /**
     * Obtiene un usuario por su numero de documento.
     *
     * @param idNumber numero de documento del usuario.
     * @return respuesta HTTP con el usuario encontrado.
     */
    @GetMapping("/users/idnumber/{idNumber}")
    @Operation(summary = "Obtener usuario por RUT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UserResponseDTO> getUserByIdNumber(@PathVariable String idNumber) {
        return ResponseEntity.ok(userService.findByIdNumber(idNumber));
    }

    /**
     * Obtiene los estudiantes asociados a un apoderado.
     *
     * @param guardianId identificador del apoderado.
     * @return respuesta HTTP con la lista de estudiantes.
     */
    @GetMapping("/users/guardian/{guardianId}")
    @Operation(summary = "Obtener estudiantes por ID del apoderado")
    @ApiResponse(responseCode = "200", description = "Lista de estudiantes asignados al apoderado")
    public ResponseEntity<List<UserResponseDTO>> getStudentsByGuardian(@PathVariable Long guardianId) {
        return ResponseEntity.ok(userService.findByGuardianId(guardianId));
    }

    /**
     * Obtiene el usuario autenticado a partir del token.
     *
     * @param token encabezado Authorization con el token.
     * @return respuesta HTTP con los datos del usuario actual.
     */
    @GetMapping("/me")
    @Operation(summary = "Obtener usuario autenticado", description = "Retorna los datos del usuario dueño del token")
    @ApiResponse(responseCode = "200", description = "Datos del usuario actual")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(authService.validateToken(token));
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id identificador del usuario.
     * @param request datos actualizados del usuario.
     * @return respuesta HTTP con el usuario actualizado.
     */
    @PutMapping("/users/{id}")
    @Operation(summary = "Actualizar usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequestDTO request) {
        return ResponseEntity.ok(userService.updateFromRequest(id, request));
    }

    /**
     * Elimina logicamente un usuario.
     *
     * @param id identificador del usuario.
     * @return respuesta HTTP sin contenido.
     */
    @DeleteMapping("/users/{id}")
    @Operation(summary = "Eliminar usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cambia la contraseña del usuario autenticado.
     *
     * @param token encabezado Authorization con el token.
     * @param request datos de cambio de contraseña.
     * @return respuesta HTTP con el usuario actualizado.
     */
    @PostMapping("/change-password")
    @Operation(summary = "Cambiar contraseña")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contraseña actualizada"),
        @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta")
    })
    public ResponseEntity<UserResponseDTO> changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChangePasswordRequestDTO request) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = authService.validateToken(token).getEmail();
        return ResponseEntity.ok(authService.changePassword(email, request));
    }
}
