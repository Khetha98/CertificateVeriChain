package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UtxoResponse {

    private String tx_hash;
    private int tx_index;
    private List<Amount> amount;

    @Getter
    @Setter
    @ToString
    public static class Amount {
        private String unit;
        private String quantity;

    }

}
