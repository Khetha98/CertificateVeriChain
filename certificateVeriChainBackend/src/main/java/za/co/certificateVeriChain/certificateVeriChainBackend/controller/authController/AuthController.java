package za.co.certificateVeriChain.certificateVeriChainBackend.controller.authController;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.User;

@Controller
@RestController
public class AuthController {


    @PostMapping("/api/auth/register")
    public ResponseEntity<String> register(@RequestBody User user){
        return ResponseEntity.ok("successfully registered");
    }


    @PostMapping("/api/auth/login")
    public ResponseEntity<String> login(@RequestBody User user){
        return ResponseEntity.ok("successfully logged in");
    }
}
