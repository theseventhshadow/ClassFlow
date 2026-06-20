package com.ohiggins.classflow.auth.service;

import com.ohiggins.classflow.auth.dto.UserResponseDTO;
import com.ohiggins.classflow.auth.dto.UpdateUserRequestDTO;
import com.ohiggins.classflow.auth.entity.User;
import com.ohiggins.classflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return convertToDTO(user);
    }

    public UserResponseDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return convertToDTO(user);
    }

    public UserResponseDTO findByIdNumber(String idNumber) {
        User user = userRepository.findByIdNumber(idNumber)
                .orElseThrow(() -> new RuntimeException("User not found with ID number: " + idNumber));
        return convertToDTO(user);
    }

    public List<UserResponseDTO> findAllByRole(String role) {
        // Implementar según sea necesario
        throw new UnsupportedOperationException("Method to be implemented - add findByRole in repository");
    }

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

    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);  // Logical delete
        userRepository.save(user);
    }

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
