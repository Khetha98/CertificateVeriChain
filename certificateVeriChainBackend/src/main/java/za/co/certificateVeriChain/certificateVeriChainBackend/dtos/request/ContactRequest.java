package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContactRequest {
    public String name;
    public String email;
    public String organization;
    public String message;
}
