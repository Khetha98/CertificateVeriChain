package za.co.certificateVeriChain.certificateVeriChainBackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class CertificateField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
