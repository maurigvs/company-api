package br.com.maurigvs.company;

import java.util.Optional;

import br.com.maurigvs.company.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByLogin(String login);

    Optional<User> findByLogin(String login);
}
