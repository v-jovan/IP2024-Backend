package org.unibl.etf.ip2024.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unibl.etf.ip2024.models.entities.ActivityEntity;
import org.unibl.etf.ip2024.models.entities.UserEntity;

import java.util.List;

@Repository
public interface ActivityEntityRepository extends JpaRepository<ActivityEntity, Integer>{
    List<ActivityEntity> findAllByUser(UserEntity user);
}
