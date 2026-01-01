package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterInstitutionDto {
    private String email;
    private String password;
    private String institutionName;
    private String registrationNumber;

}
