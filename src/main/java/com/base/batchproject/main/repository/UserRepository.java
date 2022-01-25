package com.base.batchproject.main.repository;

import com.base.batchproject.main.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

//user저장 repository
public interface UserRepository extends JpaRepository<User,Long> {
}
