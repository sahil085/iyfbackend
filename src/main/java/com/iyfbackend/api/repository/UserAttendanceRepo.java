package com.iyfbackend.api.repository;

import com.iyfbackend.api.domain.User;
import com.iyfbackend.api.domain.UserAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAttendanceRepo extends JpaRepository<UserAttendance, Long> {

    UserAttendance findByUser_Id(Long userId);
}
