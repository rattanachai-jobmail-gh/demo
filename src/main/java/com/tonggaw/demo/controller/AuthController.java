package com.tonggaw.demo.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tonggaw.demo.entity.User;
import com.tonggaw.demo.record.LoginReq;
import com.tonggaw.demo.record.RegDTO;
import com.tonggaw.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserService  userService;

    private AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();


    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    @Autowired 
    public  void setUserSerivce(UserService service){
        this.userService = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@RequestBody RegDTO newUser){
        try {
            User user = userService.registerUser(newUser);
            Map<String, String> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("firstName", user.getUserFirstName());
            response.put("lastName", user.getUserLastName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "400");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginReq loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try{    
            Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
            Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);
            User user = this.userService.findByUsername(authenticationResponse.getName());
            System.out.println("Authentication Response : \n" + authenticationResponse);
        
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationResponse);
            SecurityContextHolder.setContext(context);

            securityContextRepository.saveContext(context, request, response);
            return ResponseEntity.ok(Map.of(
                        "message", "Login success",
                        "authenticated", true,
                        "username", authenticationResponse.getName(),
                        "firstname", user != null ? user.getUserFirstName() : authenticationResponse.getName(),
                        "authorities", authenticationResponse.getAuthorities().stream()
                                .map(authority -> authority.getAuthority())
                                .collect(Collectors.toList())
                ));
        }
        catch (AuthenticationException e){
            return ResponseEntity.status(401).body("Invalid username or password");
        }

    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        User user = this.userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("firstName", user.getUserFirstName());
        response.put("lastName", user.getUserLastName());
        response.put("authorities", authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }












    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    @PostMapping("/logout")
    public ResponseEntity<?> performLogout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        // .. perform logout
        this.logoutHandler.logout(request, response, authentication);
        return ResponseEntity.ok("Logout successful");
    }

}
