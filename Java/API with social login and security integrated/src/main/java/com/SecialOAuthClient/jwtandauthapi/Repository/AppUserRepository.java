package com.eventaccess.jwtandauthapi.Repository;

import com.eventaccess.jwtandauthapi.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
}
