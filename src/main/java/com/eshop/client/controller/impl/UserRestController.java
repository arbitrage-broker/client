package com.eshop.client.controller.impl;

import com.eshop.client.config.Limited;
import com.eshop.client.filter.UserFilter;
import com.eshop.client.model.UserModel;
import com.eshop.client.repository.CountryUsers;
import com.eshop.client.service.ParameterService;
import com.eshop.client.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.eshop.client.util.StringUtils.generateIdKey;

@RestController
@Tag(name = "User Rest Service v1")
@RequestMapping(value = "/api/v1/user")
public class UserRestController extends BaseRestControllerImpl<UserFilter, UserModel, UUID> {

    private final UserService userService;
    private final SessionRegistry sessionRegistry;

    public UserRestController(UserService service, SessionRegistry sessionRegistry) {
        super(service);
        this.userService = service;
        this.sessionRegistry = sessionRegistry;
    }

    @PostMapping("/register")
    @Limited(requestsPerMinutes = 3)
    public ResponseEntity<UserModel> register(@Valid @RequestBody UserModel model) {
        return ResponseEntity.ok(userService.register(model));
    }
    @GetMapping("/verify-email/{userId}/{otp}")
    @Limited(requestsPerMinutes = 3)
    public ModelAndView verifyEmail(@PathVariable UUID userId, @PathVariable String otp) {
        ModelAndView modelAndView = new ModelAndView("page_200");
        if(userService.verifyEmail(userId, otp)){
            modelAndView.addObject("title", "Congratulations!");
            modelAndView.addObject("message", "Your email has been successfully verified!");
        } else {
            modelAndView.addObject("title", "Verification Failed!");
            modelAndView.addObject("message1", "Unfortunately, Your email verification has been failed.");
            modelAndView.addObject("message2", "It's probably because of your expired OTP code, Please try to get a fresh OTP code!");
        }

        return modelAndView;
    }
    @GetMapping("/total-online")
    public ResponseEntity<Integer> totalOnline(){
//        return ResponseEntity.ok(sessionRegistry.getAllPrincipals().size());
        return ResponseEntity.ok(new Random().nextInt(700000 - 200000 + 1) + 200000);
    }
    @GetMapping("/count-by-country")
    public ResponseEntity<List<CountryUsers>> findAllUserCountByCountry(){
        return ResponseEntity.ok(userService.findAllUserCountByCountry());
    }
}
