package com.abx.chat.repositories;

import com.abx.chat.model.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    // create a new method findByUsername
    User findByUsername(String username);

}

