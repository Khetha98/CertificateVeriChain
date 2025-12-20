package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class SigningService {

    public Path signTransaction(Path txRaw) {
        ProcessBuilder pb = new ProcessBuilder(
                "cardano-cli", "transaction", "sign",
                "--tx-body-file", txRaw.toString(),
                "--signing-key-file", "payment.skey",
                "--testnet-magic", "1",
                "--out-file", "tx.signed"
        );
        try {
            pb.start().waitFor();
        }catch(Exception ex){
            System.out.println("Error encountered");
        }
        return Path.of("tx.signed");
    }
}
