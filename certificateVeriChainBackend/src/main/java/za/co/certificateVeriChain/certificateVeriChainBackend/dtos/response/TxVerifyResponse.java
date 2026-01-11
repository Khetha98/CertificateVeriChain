package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TxVerifyResponse {
    private boolean valid;
    private String onChainHash;
    private String dbHash;
    private String txHash;
}
