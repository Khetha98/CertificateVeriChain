package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginResponse {

    private String token;
    private long expiresIn;
    private String role;

}
