package com.dnk.iam.infrastructure.persistence.repository;

import com.dnk.iam.infrastructure.persistence.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleJpaRepository extends JpaRepository<UserRoleEntity, UserRoleEntity.UserRoleId> {
    
    @Query("SELECT ur FROM UserRoleEntity ur WHERE ur.id.userId = :userId")
    List<UserRoleEntity> findByUserId(@Param("userId") String userId);
}
