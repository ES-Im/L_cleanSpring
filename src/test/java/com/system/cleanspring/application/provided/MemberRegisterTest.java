package com.system.cleanspring.application.provided;

import com.system.cleanspring.SplearnMemberTestConfiguration;
import com.system.cleanspring.domain.DuplicatedEmailException;
import com.system.cleanspring.domain.Member;
import com.system.cleanspring.domain.MemberRegisterRequest;
import com.system.cleanspring.domain.MemberStatus;
import com.system.cleanspring.domain.member.MemberFixture;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.util.Assert.isInstanceOf;

@SpringBootTest
@Transactional
@Import(SplearnMemberTestConfiguration.class)
public record MemberRegisterTest(MemberRegister memberRegister) {

    @Test
    void register() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThat(member.getId()).isNotNull();
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @Test
    void duplicateEmailFail() {
        memberRegister.register(MemberFixture.createMemberRegisterRequest());

         assertThatThrownBy(() ->
                 memberRegister.register(MemberFixture.createMemberRegisterRequest())
         ).isInstanceOf(DuplicatedEmailException.class);
    }

    @Test
    void memberRegisterRequestFail() {
        extracted(new MemberRegisterRequest("toby@splearn.app", "Toby", "longSecret"));
        extracted(new MemberRegisterRequest("toby@splearn.app", "Toby_______________________________", "longSecret"));
        extracted(new MemberRegisterRequest("tobysplearn.app", "Toby_______________________________", "longSecret"));

    }

    private void extracted(MemberRegisterRequest req) {
        assertThatThrownBy(() ->
            memberRegister.register(req)
        ).isInstanceOf(ConstraintViolationException.class);
    }
}
