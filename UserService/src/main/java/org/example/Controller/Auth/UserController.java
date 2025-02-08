package org.example.Controller.Auth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.DTO.SubscriptionDTO;
import org.example.DTO.UserDTO;
import org.example.Exception.RepeatedRegistrationException;
import org.example.Exception.WrongDataException;
import org.example.Request.User.AuthTokenRequest;
import org.example.Request.User.RegistrationRequest;
import org.example.Request.User.SubscriptionRequest;
import org.example.Response.User.AuthTokenResponse;
import org.example.Response.User.RegistrationResponse;
import org.example.Response.User.SubscriptionResponse;
import org.example.Service.SubscriptionService;
import org.example.Service.UserService;
import org.example.Utils.JwtTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {


    private final UserService userService;

    private final JwtTokenUtils jwtTokenUtils;

    private final AuthenticationManager authenticationManager;

    private final SubscriptionService subscriptionService;

    @PostMapping("/signup")
    public ResponseEntity<?> registration(@Validated @RequestBody RegistrationRequest registrationRequest) {

        if (userService.findByUsername(registrationRequest.username()).isPresent()) {
            return new ResponseEntity<>(new RepeatedRegistrationException(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует"), HttpStatus.BAD_REQUEST);
        }

        if (userService.findByEmail(registrationRequest.username()).isPresent()) {
            return new ResponseEntity<>(new RepeatedRegistrationException(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным email уже существует"), HttpStatus.BAD_REQUEST);
        }

        UserDTO userDTO = new UserDTO(registrationRequest.username(), registrationRequest.email(), registrationRequest.password());
        userService.createNewUser(userDTO);

        return ResponseEntity.ok(new RegistrationResponse(userDTO.username(), userDTO.email()));
    }


    @PostMapping("/secured/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody SubscriptionRequest subscriptionRequest) {
      return   subscriptionService.subscribe(subscriptionRequest);
    }


    @GetMapping("/secured/checkToken")
    public boolean checkToken(){
        return  true;
    }

    @PostMapping("/createAuthToken")
    public ResponseEntity<?> createAuthToken(@Validated @RequestBody AuthTokenRequest authTokenRequest) {
        log.info("{} requested token", authTokenRequest.username());
        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authTokenRequest.username(), authTokenRequest.password()));

        } catch (BadCredentialsException e) {

            return new ResponseEntity<>(
                    new WrongDataException(HttpStatus.UNAUTHORIZED.value(),
                            "Incorrect login or password"), HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = userService.loadUserByUsername(authTokenRequest.username());
        String token = jwtTokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(new AuthTokenResponse(token));
    }


}
