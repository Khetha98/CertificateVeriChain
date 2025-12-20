package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class TransactionBuilderService {

    public Path buildTransaction(Path metadataFile) {
        ProcessBuilder pb = new ProcessBuilder(
                "cardano-cli", "transaction", "build",
                "--babbage-era",
                "--testnet-magic", "1",
                "--metadata-json-file", metadataFile.toString(),
                "--out-file", "tx.raw"
        );

        try {
            pb.start().waitFor();
        }catch(Exception ex){
            System.out.println("Error encountered");
        }
        return Path.of("tx.raw");
    }
}

