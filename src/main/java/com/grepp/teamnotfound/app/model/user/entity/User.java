package com.grepp.teamnotfound.app.model.user.entity;

import com.grepp.teamnotfound.app.model.auth.code.Role;
import com.grepp.teamnotfound.app.model.user.code.SuspensionPeriod;
import com.grepp.teamnotfound.app.model.user.code.UserStateResponse;
import com.grepp.teamnotfound.infra.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;


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
    @Column(length = 200)
    private String password;

    @Column(length = 20)
    private String provider;

    @Column
    private OffsetDateTime suspensionEndAt;


    public void suspend(SuspensionPeriod period) {
        if (period.isPermanent()) {
            this.suspensionEndAt = OffsetDateTime.MAX;
            super.updatedAt = OffsetDateTime.now();
            return;
        }
        OffsetDateTime now = OffsetDateTime.now();
        if (this.suspensionEndAt == null || this.suspensionEndAt.isBefore(now)) {
            this.suspensionEndAt = now.plusDays(period.getDays());
            super.updatedAt = OffsetDateTime.now();
        } else {
            this.suspensionEndAt = this.suspensionEndAt.plusDays(period.getDays());
            super.updatedAt = OffsetDateTime.now();
        }
    }

    public UserStateResponse getUserState() {
        if (this.deletedAt != null) {
            return UserStateResponse.LEAVE;
        } else if(this.suspensionEndAt == null || this.suspensionEndAt.isBefore(OffsetDateTime.now())){
            return UserStateResponse.ACTIVE;
        } else
            return UserStateResponse.SUSPENDED;
        }
    }
