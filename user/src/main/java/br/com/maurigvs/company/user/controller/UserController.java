package br.com.maurigvs.company.user.controller;

import br.com.maurigvs.company.user.exception.TechnicalException;
import br.com.maurigvs.company.user.model.UserResponse;
import br.com.maurigvs.company.user.service.UserService;
import br.com.maurigvs.company.user.exception.BusinessException;
import br.com.maurigvs.company.user.model.UserRequest;
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