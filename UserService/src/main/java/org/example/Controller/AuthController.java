package org.example.Controller;

import lombok.AllArgsConstructor;
import org.example.DTO.UserDTO;
import org.example.Exception.RepeatedRegistrationException;
import org.example.Exception.WrongDataException;
import org.example.Request.Auth.AuthTokenRequest;
import org.example.Response.Auth.AuthTokenResponse;
import org.example.Service.UserService;
import org.example.Utils.JwtTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthController {


    private final UserService userService;

    private final JwtTokenUtils jwtTokenUtils;

    private  final AuthenticationManager authenticationManager;


    @PostMapping("/registration")
    public ResponseEntity<?>  registration(@RequestBody AuthTokenRequest authTokenRequest){

        if(userService.findByUsername(authTokenRequest.username()).isPresent()){
            return new ResponseEntity<>(new RepeatedRegistrationException(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует"), HttpStatus.BAD_REQUEST);
        }

        UserDTO userDTO = new UserDTO(authTokenRequest.username(),authTokenRequest.email(),authTokenRequest.password());
        return ResponseEntity.ok(userService.createNewUser(userDTO));


    }
    @GetMapping("/secured")
    public String secured(){
        return  "Secured data";
    }

    @PostMapping("/createAuthToken")
    public ResponseEntity<?> createAuthToken(@RequestBody AuthTokenRequest authTokenRequest) {

        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authTokenRequest.username(), authTokenRequest.password()));

        } catch (BadCredentialsException e) {

            return new ResponseEntity<>(
                    new WrongDataException(HttpStatus.UNAUTHORIZED.value(),
                    "Incorrect login or password"),HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = userService.loadUserByUsername(authTokenRequest.username());
        String token = jwtTokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(new AuthTokenResponse(token));
    }


}
