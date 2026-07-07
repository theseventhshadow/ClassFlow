package com.ohiggins.classflow.auth.repository;

import com.ohiggins.classflow.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    void deleteByUserId(Long userId);

    /**
     * Marca el token como usado solo si todavia no lo estaba, de forma atomica.
     * Devuelve la cantidad de filas afectadas: 0 significa que otra peticion ya lo habia consumido.
     */
    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.id = :id AND t.used = false")
    int markUsedIfNotUsed(@Param("id") Long id);
}
