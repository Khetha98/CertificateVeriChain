package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import java.security.MessageDigest;
import java.util.HexFormat;

public class CertificateHasher {

    public static String sha256(byte[] fileBytes) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(fileBytes);
        }catch(Exception ex){
            System.out.println("Error encountered");
        }
        return HexFormat.of().formatHex(hash);
    }
}

