package org.unibl.etf.ip2024.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unibl.etf.ip2024.models.entities.UserEntity;
import org.unibl.etf.ip2024.models.enums.Roles;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
    Optional<List<UserEntity>> findAllByRole(Roles role);
    Optional<List<UserEntity>> findAllByRoleNotAndUsernameNot(Roles role, String username);
}
