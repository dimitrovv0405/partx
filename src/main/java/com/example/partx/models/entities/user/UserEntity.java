package com.example.partx.models.entities.user;

import com.example.partx.models.entities.order.OrderEntity;
import com.example.partx.models.enums.user.CountryOrigin;
import com.example.partx.models.enums.user.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Lob
    @Column(name = "profile_picture", columnDefinition = "LONGBLOB")
    private byte[] profilePicture;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserType role;

    @Enumerated(EnumType.STRING)
    private CountryOrigin country;

    @Column(name = "user_balance")
    private BigDecimal userBalance;

    @OneToMany(mappedBy = "user")
    private List<OrderEntity> orders;
}