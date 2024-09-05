package org.unibl.etf.ip2024.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unibl.etf.ip2024.models.entities.UserEntity;
import org.unibl.etf.ip2024.models.entities.UserProgramEntity;

@Repository
public interface UserProgramEntityRepository extends JpaRepository<UserProgramEntity, Integer> {
    Page<UserProgramEntity> findAllByUserByUserId(UserEntity user, Pageable pageable);
}
