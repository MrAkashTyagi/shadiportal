package com.bigsquare.ShadiPortal.repositories;

import com.bigsquare.ShadiPortal.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {
}
