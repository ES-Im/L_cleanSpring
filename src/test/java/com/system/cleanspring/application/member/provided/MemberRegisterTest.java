package com.system.cleanspring.application.member.provided;

import com.system.cleanspring.SplearnMemberTestConfiguration;
import com.system.cleanspring.application.member.required.MemberRepository;
import com.system.cleanspring.domain.member.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Import(SplearnMemberTestConfiguration.class)
public record MemberRegisterTest(MemberRegister memberRegister, MemberRepository memberRepository, EntityManager entityManager) {

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

    @Test
    void activate() {
        Member member = registerMember();

        member = memberRegister.activate(member.getId());

        entityManager.flush();

        var found = memberRepository.findById(member.getId()).orElseThrow();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(found.getDetail().getRegisteredAt()).isNotNull();
    }

    @Test
    void deactivate() {
        Member member = registerMember();

        memberRegister.activate(member.getId());
        entityManager.flush();
        entityManager.clear();

        memberRegister.deactivate(member.getId());
        entityManager.flush();
        entityManager.clear();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);

//        assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
    }

    @Test
    void updateInfo() {
        Member member = registerMember();

        memberRegister.activate(member.getId());
        entityManager.flush();
        entityManager.clear();

        member = memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("Leo11", "toby10000", "자기소개"));
        entityManager.flush();
        entityManager.clear();

        assertThat(member.getDetail().getProfile().address()).isEqualTo("toby10000");
    }

    @Test
    void updateInfo_fail() {
        Member member = registerMember();
        memberRegister.activate(member.getId());
        member = memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("Leo11", "toby10000", "자기소개"));

        Member member2 = registerMember("toby2@splearn.app");
        entityManager.flush();
        entityManager.clear();

        assertThatThrownBy(() ->
                memberRegister.updateInfo(member2.getId(), new MemberInfoUpdateRequest("James", "toby10000", "자기소개"))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    private Member registerMember() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
        entityManager.flush();
        entityManager.clear();
        return member;
    }
    private Member registerMember(String email) {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest(email));
        entityManager.flush();
        entityManager.clear();
        return member;
    }

}
