package com.grepp.teamnotfound.app.model.user.entity;

import com.grepp.teamnotfound.app.model.auth.code.Role;
import com.grepp.teamnotfound.infra.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.*;


@Builder
@Entity
@Table(name = "Users")
@Getter
//@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
        name = "primary_sequence",
        sequenceName = "primary_sequence",
        allocationSize = 1,
        initialValue = 10000
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "primary_sequence"
    )
    private Long userId;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Builder.Default
    @Column(nullable = false)
    private Boolean state = true;

    @Setter
    @Column(nullable = false, length = 10)
    private String name;

    @Setter
    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Setter
    @Column(
//            nullable = false,
            length = 200)
    private String password;

    @Column(length = 20)
    private String provider;

}