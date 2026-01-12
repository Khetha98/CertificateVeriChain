package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import com.bloxbean.cardano.client.metadata.cbor.CBORMetadata;
import com.bloxbean.cardano.client.metadata.cbor.CBORMetadataMap;
import com.bloxbean.cardano.client.spec.NetworkId;
import com.bloxbean.cardano.client.transaction.spec.*;
import org.springframework.stereotype.Service;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.UtxoResponse;

import java.math.BigInteger;
import java.util.List;

import com.bloxbean.cardano.client.transaction.spec.*;

@Service
public class TxBuilderService {

    private static final BigInteger FIXED_FEE = BigInteger.valueOf(400_000);
    private static final BigInteger SAFE_MIN_UTXO = BigInteger.valueOf(8_000_000);
    private static final long TTL_OFFSET = 1_000;

    public Transaction buildTx(
            List<UtxoResponse> utxos,
            String senderAddress,
            String certificateHash,
            long currentSlot
    ) {
        // Pick first safe ADA-only UTXO from issuer.addr
        UtxoResponse utxo = utxos.stream()
                .filter(u -> u.getAmount().size() == 1 &&
                        "lovelace".equals(u.getAmount().get(0).getUnit()) &&
                        new BigInteger(u.getAmount().get(0).getQuantity()).compareTo(SAFE_MIN_UTXO) >= 0
                )
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No suitable ADA-only UTXO found for " + senderAddress));

        BigInteger inputLovelace = new BigInteger(utxo.getAmount().get(0).getQuantity());
        BigInteger change = inputLovelace.subtract(FIXED_FEE);

        if (change.compareTo(SAFE_MIN_UTXO) < 0) {
            throw new IllegalStateException("Change below safe min ADA: " + change);
        }

        // Build transaction body
        TransactionBody body = new TransactionBody();
        body.setInputs(List.of(new TransactionInput(utxo.getTx_hash(), utxo.getTx_index())));
        body.setOutputs(List.of(new TransactionOutput(senderAddress, Value.fromCoin(change))));
        body.setFee(FIXED_FEE);
        body.setTtl(currentSlot + TTL_OFFSET);
        body.setNetworkId(NetworkId.TESTNET);

        // Add metadata
        CBORMetadataMap metadataMap = new CBORMetadataMap().put("certificateHash", certificateHash);
        CBORMetadata metadata = new CBORMetadata().put(BigInteger.valueOf(6767), metadataMap);
        AuxiliaryData auxiliaryData = new AuxiliaryData();
        auxiliaryData.setMetadata(metadata);

        // Final transaction
        Transaction tx = new Transaction();
        tx.setBody(body);
        tx.setAuxiliaryData(auxiliaryData);

        return tx;
    }
}

