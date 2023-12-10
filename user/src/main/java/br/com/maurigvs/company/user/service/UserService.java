package br.com.maurigvs.company.user.service;

import br.com.maurigvs.company.user.repository.EmployeeRepository;
import br.com.maurigvs.company.user.repository.UserRepository;
import br.com.maurigvs.company.user.exception.BusinessException;
import br.com.maurigvs.company.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public User create(String login) throws BusinessException {
        if(existsByLogin(login))
            throw new BusinessException("The user is already registered");

        if(userNotEmployee(login))
            throw new BusinessException("The user must be a employee registered");

        return save(new User(null, login));
    }

    private boolean userNotEmployee(String login) {
        return !employeeRepository.existsByEmailAddress(login).getExists();
    }

    private boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    private User save(User user){
        return userRepository.save(user);
    }
}