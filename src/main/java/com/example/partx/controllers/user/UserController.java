package com.example.partx.controllers.user;

import com.example.partx.models.dtos.user.UserDto;
import com.example.partx.models.entities.user.UserEntity;
import com.example.partx.services.user.UserService;
import com.example.partx.mappers.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ModelAndView showProfilePage(Principal principal) {
        ModelAndView modelAndView = new ModelAndView("profile");

        String currentUsername = principal.getName();

        UserEntity userEntity = userService.findUserEntityByUsername(currentUsername);

        UserDto userDto = UserMapper.toUserDto(userEntity);

        modelAndView.addObject("username", userDto.getUsername());
        modelAndView.addObject("email", userDto.getEmail());
        modelAndView.addObject("userRole", userDto.getRole());
        modelAndView.addObject("balance",
                userDto.getUserBalance() != null ? userDto.getUserBalance() : java.math.BigDecimal.ZERO);

        modelAndView.addObject("profilePic", userDto.getProfilePicture());

        return modelAndView;
    }

    @PostMapping("/profile/update-info")
    public ModelAndView updateAccountInfo(
            @RequestParam("username") String newUsername,
            @RequestParam("email") String newEmail,
            @RequestParam("role") String newUserRole,
            Principal principal
    ) {
        ModelAndView modelAndView = new ModelAndView(
                new RedirectView("/users/profile", true));

        try {
            String currentUsername = principal.getName();

            userService.updateUserInfo(currentUsername, newUsername, newEmail, newUserRole);
            modelAndView.addObject("infoSuccess", "Account information updated successfully!");
        } catch (Exception e) {
            modelAndView.addObject("infoError",
                    "Error updating profile: " + e.getMessage());
        }

        return modelAndView;
    }

    @PostMapping("/profile/change-password")
    public ModelAndView changePassword(
            @RequestParam("current-password") String currentPassword,
            @RequestParam("new-password") String newPassword,
            @RequestParam("confirm-password") String confirmPassword,
            Principal principal
    ) {
        ModelAndView modelAndView = new ModelAndView(
                new RedirectView("/users/profile", true)
        );
        
        try {
            String currentUsername = principal.getName();

            userService.updateUserPassword(currentUsername, currentPassword, newPassword, confirmPassword);
            modelAndView.addObject("passwordSuccess",
                    "Password updated successfully!");
        } catch (Exception e) {
            modelAndView.addObject("passwordError",
                    "Error updating password: " + e.getMessage());
        }

        return modelAndView;
    }

    @PostMapping("/profile/add-money")
    public ModelAndView addMoney(
            @RequestParam("amount") double amount,
            Principal principal
    ) {
        ModelAndView modelAndView = new ModelAndView(new RedirectView("/users/profile", true));
        try {
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be greater than zero.");
            }
            String currentUsername = principal.getName();
            userService.addMoneyToWallet(currentUsername, amount);
            modelAndView.addObject("infoSuccess", "Successfully added $" + String.format("%.2f", amount) + " to your wallet!");
        } catch (Exception e) {
            modelAndView.addObject("infoError", "Failed to add money: " + e.getMessage());
        }
        return modelAndView;
    }

    @PostMapping("/profile/upload-picture")
    public ModelAndView uploadProfilePicture(
            @RequestParam("profile-pic-input") MultipartFile file,
            Principal principal
    ) {
        ModelAndView modelAndView = new ModelAndView(new RedirectView("/users/profile", true));
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Please select a file to upload.");
            }

            String currentUsername = principal.getName();
            userService.saveProfilePicture(currentUsername, file);

            modelAndView.addObject("infoSuccess", "Profile picture updated successfully!");
        } catch (Exception e) {
            modelAndView.addObject("infoError", "Failed to upload image: " + e.getMessage());
        }
        return modelAndView;
    }
}