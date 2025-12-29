package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginUserDto {
    private String email;
    private String password;

}
