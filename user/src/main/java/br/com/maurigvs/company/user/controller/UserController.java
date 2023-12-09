package br.com.maurigvs.company.user.controller;

import br.com.maurigvs.company.user.service.UserService;
import br.com.maurigvs.company.user.exception.BusinessException;
import br.com.maurigvs.company.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public void postEmployee(@RequestBody UserDto request) throws BusinessException {
        userService.create(request.getLogin());
    }
}