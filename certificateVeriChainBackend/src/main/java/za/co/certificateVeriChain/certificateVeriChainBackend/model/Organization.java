package za.co.certificateVeriChain.certificateVeriChainBackend.model;

import jakarta.persistence.*;


import java.util.List;

@Entity
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private String logoUrl;
    private String address;
    private String phone;
    private String website;
    private String createdAt;
    private String updatedAt;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<User> users;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<CertificateTemplate> templates;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<Certificate> certificates;

}
