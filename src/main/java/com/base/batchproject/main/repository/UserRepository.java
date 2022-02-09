package com.base.batchproject.main.repository;

import com.base.batchproject.main.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;

//user저장 repository
public interface UserRepository extends JpaRepository<User,Long> {

    //Collection<Object> findAllByUpdateDate(LocalDate updateDate);
}
