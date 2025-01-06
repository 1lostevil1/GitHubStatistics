package org.example.Controller;

import org.example.DTO.UserDTO;
import org.example.Request.Auth.AuthRequest;
import org.example.Response.Auth.AuthResponse;
import org.example.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        UserDTO userDTO = authService.login(authRequest.username(),authRequest.email(),authRequest.password().hashCode());
       return  new AuthResponse(userDTO.username(),userDTO.passwordHash());
    }
    @GetMapping("/getInto")
    public UserDTO getInto(@RequestParam String email) {
        return authService.getInfo(email);
    }

}
