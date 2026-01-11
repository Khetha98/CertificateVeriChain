package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkMintResult {
    private int total;
    private int minted;
    private List<String> failedRows;
}

