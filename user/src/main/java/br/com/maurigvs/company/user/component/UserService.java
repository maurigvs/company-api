package br.com.maurigvs.company.user.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(String login) {
        return save(new User(null, login));
    }

    private User save(User user){
        return userRepository.save(user);
    }
}