package com.system.cleanspring.application.member.provided;

import com.system.cleanspring.SplearnMemberTestConfiguration;
import com.system.cleanspring.domain.member.Member;
import com.system.cleanspring.domain.member.MemberFixture;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Import(SplearnMemberTestConfiguration.class)
record MemberFinderTest(MemberFinder memberFinder, MemberRegister memberRegister, EntityManager entityManager) {

    @Test
    void find() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        entityManager.flush();
        entityManager.clear();

        Member found = memberFinder.find(member.getId());

        assertThat(found.getId()).isEqualTo(member.getId());
    }

    @Test
    void findFail() {
        assertThatThrownBy(() ->
            memberFinder.find(999L)
        ).isInstanceOf(IllegalArgumentException.class);
    }




}