package br.com.maurigvs.company.user.service;

import br.com.maurigvs.company.employee.EmployeeReply;
import br.com.maurigvs.company.user.exception.BusinessException;
import br.com.maurigvs.company.user.exception.TechnicalException;
import br.com.maurigvs.company.user.model.User;
import br.com.maurigvs.company.user.model.UserResponse;
import br.com.maurigvs.company.user.repository.EmployeeRepository;
import br.com.maurigvs.company.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public User create(String login) throws BusinessException, TechnicalException {
        if(existsByLogin(login))
            throw new BusinessException("The user is already registered");

        final var employee = findByEmailAddress(login);
        if(employee.isEmpty())
            throw new BusinessException("The user must be a employee registered");

        return save(new User(null, login, employee.get().getId()));
    }

    private Optional<EmployeeReply> findByEmailAddress(String login) throws TechnicalException {
        return employeeRepository.findByEmailAddress(login);
    }

    private boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    private User save(User user){
        return userRepository.save(user);
    }

    public UserResponse getByLogin(String login) throws BusinessException, TechnicalException {
        final var user = findByLogin(login);
        final var employee = findEmployeeByLogin(login);
        return new UserResponse(employee.getFullName(), user.getLogin());
    }

    private User findByLogin(String login) throws BusinessException {
        try{
            return userRepository.findByLogin(login).orElseThrow();
        } catch (NoSuchElementException ex){
            throw new BusinessException("User not found");
        }
    }

    private EmployeeReply findEmployeeByLogin(String login) throws BusinessException, TechnicalException {
        final var employee = employeeRepository.findByEmailAddress(login);
        if(employee.isEmpty())
            throw new BusinessException("User's information is missing");
        return employee.get();
    }
}