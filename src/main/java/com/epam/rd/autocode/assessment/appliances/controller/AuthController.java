package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.exceptions.expected.EntityExistsByEmailException;
import com.epam.rd.autocode.assessment.appliances.model.dto.LoginUserDTO;
import com.epam.rd.autocode.assessment.appliances.model.dto.client.ClientCreateDTO;
import com.epam.rd.autocode.assessment.appliances.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.epam.rd.autocode.assessment.appliances.controller.CommonNames.*;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

    private final AuthService service;


    // both for client and employee
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute(LOGIN_DTO) LoginUserDTO loginUser,
                        BindingResult result,
                        HttpServletResponse response,
                        Model model) {
        if (result.hasErrors()) {
            prepareErrorModel(model, true, false);
            return INDEX_PAGE;
        }

        Cookie jwtCookie;
        try {
            jwtCookie = service.login(loginUser);
            response.addCookie(jwtCookie);
            return REDIRECT_TO_HOME;
        } catch (BadCredentialsException e) {
            result.rejectValue("password", "bad.credentials");
            prepareErrorModel(model, true, false);
            return INDEX_PAGE;
        }
    }

    @PostMapping("/signup")
    public String signupForClient(@Valid @ModelAttribute(CREATE_DTO) ClientCreateDTO createClient,
                         BindingResult result,
                         Model model,
                         HttpServletResponse response) {

        if (result.hasErrors()) {
            prepareErrorModel(model, false, true);
            return INDEX_PAGE;
        }
        try {
            Cookie jwtCookie = service.register(createClient);
            response.addCookie(jwtCookie);
            return REDIRECT_TO_HOME;
        } catch (EntityExistsByEmailException e) {
            result.rejectValue("email", "validation.email.already-exist"); // add error to result
            prepareErrorModel(model, false, true);
            return INDEX_PAGE;
        }
    }

    @GetMapping("/custom-logout")
    public String logout(HttpServletResponse response, Authentication authentication) {
        Cookie cookie = service.logout(authentication);
        response.addCookie(cookie);
        return REDIRECT_TO_HOME;
    }

    @GetMapping("/password-reset")
    public String passwordReset(@RequestParam(required = false) String token, Model model) {
        if (token != null && service.isValidSetupPasswordToken(token)) {
            model.addAttribute("token", token);
            return "employee/password-reset";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/password-reset/confirm")
    public String handlePasswordReset(
            @RequestParam String token,
            @RequestParam String newPassword, HttpServletResponse response) {

        if (service.isValidSetupPasswordToken(token)) {
            Cookie cookie = service.updatePasswordForEmployee(token, newPassword);
            response.addCookie(cookie);

            return REDIRECT_TO_HOME;
        }
        return "error/404";
    }


    private void prepareErrorModel(
            Model model,
            boolean showLogin,
            boolean showRegister) {

        if (showLogin) {
            model.addAttribute(CREATE_DTO, new ClientCreateDTO());
        }

        if (showRegister) {
            model.addAttribute(LOGIN_DTO, new LoginUserDTO());
        }
        model.addAttribute("showLoginPopup", showLogin);
        model.addAttribute("showRegisterPopup", showRegister);
    }
}


// private void prepareErrorModel(
//            Model model,
//            BindingResult bindingResult,
//            boolean showLogin,
//            boolean showRegister,
//            Object errorDTO) {
//
//        List<String> errors = bindingResult.getAllErrors()
//                .stream()
//                .map(DefaultMessageSourceResolvable::getDefaultMessage)
//                .collect(Collectors.toList());
//
//        if (showLogin) {
//            model.addAttribute("loginErrors", errors);
//            model.addAttribute(LOGIN_DTO, errorDTO);
//            model.addAttribute(CREATE_DTO, new ClientCreateDTO());
//        }
//
//        if (showRegister) {
//            model.addAttribute("registerErrors", errors);
//            model.addAttribute(CREATE_DTO, errorDTO);
//            model.addAttribute(LOGIN_DTO, new LoginUserDTO());
//        }
//        model.addAttribute("showLoginPopup", showLogin);
//        model.addAttribute("showRegisterPopup", showRegister);
//    }