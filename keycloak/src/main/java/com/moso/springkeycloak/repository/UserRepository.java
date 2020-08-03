package com.moso.springkeycloak.repository;

import com.moso.springkeycloak.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<Users, Long> {
    List<Users> findUserByUsername(String username );
    List<Users> findAll();
    Users findByUsername(String username);
}
