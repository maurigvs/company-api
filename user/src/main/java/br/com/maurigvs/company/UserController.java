package br.com.maurigvs.company;

import br.com.maurigvs.company.exception.TechnicalException;
import br.com.maurigvs.company.model.UserResponse;
import br.com.maurigvs.company.exception.BusinessException;
import br.com.maurigvs.company.model.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postUser(@RequestBody UserRequest request) throws BusinessException, TechnicalException {
        userService.create(request.login());
    }

    @GetMapping("/{login}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserByLogin(@PathVariable String login) throws BusinessException, TechnicalException {
        return userService.getByLogin(login);
    }
}