package za.co.certificateVeriChain.certificateVeriChainBackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String fullName;
    private String role;
    private String status;
    private String dateJoined;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

}
