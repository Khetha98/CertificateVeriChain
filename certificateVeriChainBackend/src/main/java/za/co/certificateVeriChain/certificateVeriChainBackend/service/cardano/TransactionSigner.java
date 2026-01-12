package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.common.model.Networks;
import com.bloxbean.cardano.client.crypto.Bech32;
import com.bloxbean.cardano.client.transaction.spec.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;

@Service
public class TransactionSigner {

    private final Account account;

    public TransactionSigner(
            @Value("${cardano.issuer.skey}") String skeyPath,
            @Value("${cardano.network}") String network
    ) {
        try {
            File skeyFile = new File(skeyPath);
            if (!skeyFile.exists()) {
                throw new IllegalStateException("issuer.skey file not found at " + skeyPath);
            }

            // Read the root_xsk string from issuer.skey
            String rootKeyBech32 = Files.readString(skeyFile.toPath()).trim();

            // Decode Bech32
            Bech32.Bech32Data bech32Data = Bech32.decode(rootKeyBech32);
            byte[] skeyBytes = bech32Data.data;

            // Initialize Account
            this.account = Account.createFromRootKey(
                    "mainnet".equalsIgnoreCase(network) ? Networks.mainnet() : Networks.testnet(),
                    skeyBytes
            );


            System.out.println("TransactionSigner initialized for account: " + account.baseAddress());

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize TransactionSigner", e);
        }
    }

    /** Signs the transaction using issuer.skey */
    public byte[] sign(Transaction tx) {
        try {
            Transaction signedTx = account.sign(tx);
            return signedTx.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign transaction", e);
        }
    }

    /** Get the issuer address for UTXO selection */
    public String getAddress() {
        System.out.println("Address from root key: " + account.baseAddress());
        return account.baseAddress();
    }
}
