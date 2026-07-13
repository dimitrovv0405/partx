package com.example.partx.controllers.auth;

import com.example.partx.models.dtos.user.LoginRequestDto;
import com.example.partx.models.dtos.user.RegisterDto;
import com.example.partx.models.dtos.user.RegisterFormData;
import com.example.partx.models.enums.user.CountryOrigin;
import com.example.partx.models.enums.user.UserType;
import com.example.partx.services.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final UserService userService;

    @GetMapping("/login")
    public ModelAndView getLogin() {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("userLoginData", LoginRequestDto.builder().build());
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView getRegistration() {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("userRegisterData", RegisterFormData.builder().build());
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid @ModelAttribute("userRegisterData") RegisterFormData userRegisterData,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("register");
            mav.addObject("userRegisterData", userRegisterData);
            return mav;
        }

        RegisterDto dto = new RegisterDto();
        dto.setFirstName(userRegisterData.getFirstName());
        dto.setLastName(userRegisterData.getLastName());
        dto.setUsername(userRegisterData.getUsername());
        dto.setEmail(userRegisterData.getEmail());
        dto.setPassword(userRegisterData.getPassword());
        dto.setConfirmPassword(userRegisterData.getConfirmPassword());
        dto.setUserType(UserType.valueOf(userRegisterData.getUserType()));
        dto.setCountryOrigin(CountryOrigin.valueOf(userRegisterData.getCountryOrigin()));

        // logger.info(String.format("IndexController register() dto.firstName = %s", dto.getFirstName()));


        userService.registerUser(dto);
        return new ModelAndView("redirect:/login");
    }
}
