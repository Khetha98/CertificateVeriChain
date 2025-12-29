package za.co.certificateVeriChain.certificateVeriChainBackend.controller.authController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.User;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.AuthService;

@Controller
@RestController
public class AuthController {
    @Autowired
    AuthService authService;


    @PostMapping("/api/auth/register")
    public ResponseEntity<String> register(@RequestBody User user){
        return ResponseEntity.ok(authService.registerService());
    }


    @PostMapping("/api/auth/login")
    public ResponseEntity<String> login(@RequestBody User user){

        return ResponseEntity.ok(authService.loginService());
    }
}
