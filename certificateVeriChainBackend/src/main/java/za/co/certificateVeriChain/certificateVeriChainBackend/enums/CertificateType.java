package za.co.certificateVeriChain.certificateVeriChainBackend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CertificateType {
    MATRIC, DEGREE, DIPLOMA, SETA, TRADE_TEST, BBBEE, POLICE_CLEARANCE, OTHER;

    @JsonCreator
    public static CertificateType fromString(String value) {
        return CertificateType.valueOf(value.toUpperCase());
    }
}

