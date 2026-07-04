package com.ohiggins.classflow.auth.service;

import com.ohiggins.classflow.auth.dto.UserResponseDTO;
import com.ohiggins.classflow.auth.dto.UpdateUserRequestDTO;
import com.ohiggins.classflow.auth.entity.User;
import com.ohiggins.classflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Contiene la logica de negocio para la gestion de usuarios.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Busca un usuario por su identificador.
     *
     * @param id identificador del usuario.
     * @return usuario encontrado convertido a DTO.
     */
    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return convertToDTO(user);
    }

    /**
     * Busca un usuario por su correo electronico.
     *
     * @param email correo electronico del usuario.
     * @return usuario encontrado convertido a DTO.
     */
    public UserResponseDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return convertToDTO(user);
    }

    /**
     * Busca un usuario por su numero de documento.
     *
     * @param idNumber numero de documento del usuario.
     * @return usuario encontrado convertido a DTO.
     */
    public UserResponseDTO findByIdNumber(String idNumber) {
        User user = userRepository.findByIdNumber(idNumber)
                .orElseThrow(() -> new RuntimeException("User not found with ID number: " + idNumber));
        return convertToDTO(user);
    }

    /**
     * Busca usuarios por rol.
     *
     * @param role rol a buscar.
     * @return lista de usuarios convertidos a DTO.
     */
    public List<UserResponseDTO> findAllByRole(String role) {
        // Implementar según sea necesario
        throw new UnsupportedOperationException("Method to be implemented - add findByRole in repository");
    }

    /**
     * Busca los estudiantes asociados a un apoderado.
     *
     * @param guardianId identificador del apoderado.
     * @return lista de estudiantes convertidos a DTO.
     */
    public List<UserResponseDTO> findByGuardianId(Long guardianId) {
        List<User> students = userRepository.findByGuardianId(guardianId);
        return students.stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Actualiza un usuario a partir de un DTO de respuesta.
     *
     * @param id identificador del usuario.
     * @param dto datos actualizados del usuario.
     * @return usuario actualizado convertido a DTO.
     */
    public UserResponseDTO update(Long id, UserResponseDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setCourse(dto.getCourse());
        
        User updated = userRepository.save(user);
        return convertToDTO(updated);
    }

    /**
     * Actualiza un usuario a partir de la solicitud de edicion.
     *
     * @param id identificador del usuario.
     * @param request datos actualizados del usuario.
     * @return usuario actualizado convertido a DTO.
     */
    public UserResponseDTO updateFromRequest(Long id, UpdateUserRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setCourse(request.getCourse());
        
        User updated = userRepository.save(user);
        return convertToDTO(updated);
    }

    /**
     * Desactiva logicamente un usuario.
     *
     * @param id identificador del usuario.
     */
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);  // Logical delete
        userRepository.save(user);
    }

    /**
     * Convierte una entidad usuario a DTO de respuesta.
     *
     * @param user entidad usuario.
     * @return DTO de respuesta.
     */
    public UserResponseDTO convertToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .idNumber(user.getIdNumber())
                .email(user.getEmail())
                .role(user.getRole().name())
                .course(user.getCourse())
                .active(user.getActive())
                .build();
    }
}
