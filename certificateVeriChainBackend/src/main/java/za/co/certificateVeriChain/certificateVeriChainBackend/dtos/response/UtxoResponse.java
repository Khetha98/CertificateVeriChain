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
    private List<AmountDto> amount;

    public String getTx_hash() {
        return tx_hash;
    }

    public int getTx_index() {
        return tx_index;
    }

    public List<AmountDto> getAmount() {
        return amount;
    }

    // 🔥 ADD THIS METHOD
    public long getLovelace() {
        return amount.stream()
                .filter(a -> "lovelace".equals(a.getUnit()))
                .mapToLong(a -> Long.parseLong(a.getQuantity()))
                .findFirst()
                .orElse(0L);
    }
}
