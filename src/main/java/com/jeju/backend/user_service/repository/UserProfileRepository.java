package com.jeju.backend.user_service.repository;

import com.jeju.backend.user_service.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

}
