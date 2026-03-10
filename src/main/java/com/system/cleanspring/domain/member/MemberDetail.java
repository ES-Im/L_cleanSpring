package com.system.cleanspring.domain.member;

import com.system.cleanspring.domain.shared.AbstractEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class MemberDetail extends AbstractEntity {

    private Profile profile;

    private String introduction;

    private LocalDateTime registeredAt;

    private LocalDateTime activatedAt;

    private LocalDateTime deactivatedAt;

    static MemberDetail create() {
        MemberDetail de = new MemberDetail();
        de.registeredAt = LocalDateTime.now();
        return de;
    }

    void activate() {
        Assert.isTrue(activatedAt == null, "Activated at is already set");

        this.activatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        Assert.isTrue(deactivatedAt == null, "deActivated at is already set");

        this.deactivatedAt = LocalDateTime.now();
    }

    public void updateInfo(MemberInfoUpdateRequest updateRequest) {
        this.profile = new Profile(updateRequest.profileAddress());
        this.introduction = updateRequest.introduction();
    }
}
