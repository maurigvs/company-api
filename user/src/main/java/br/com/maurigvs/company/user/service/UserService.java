package br.com.maurigvs.company.user.service;

import br.com.maurigvs.company.user.repository.UserRepository;
import br.com.maurigvs.company.user.exception.BusinessException;
import br.com.maurigvs.company.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(String login) throws BusinessException {
        return save(new User(null, login));
    }

    private User save(User user) throws BusinessException {
        if(userRepository.existsUserByLogin(user.getLogin()))
            throw new BusinessException("The user is already registered");

        return userRepository.save(user);
    }
}