package com.system.cleanspring.application.provided;

import com.system.cleanspring.application.MemberService;
import com.system.cleanspring.application.required.EmailSender;
import com.system.cleanspring.application.required.MemberRepository;
import com.system.cleanspring.domain.Email;
import com.system.cleanspring.domain.Member;
import com.system.cleanspring.domain.MemberStatus;
import com.system.cleanspring.domain.member.MemberFixture;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class MemberRegisterManualTest {

    // Stub
    @Test
    void registerTestStub() {
        MemberService memberService = new MemberService(
                new MemberRepositoryStub(), new EmailSenderStub(), MemberFixture.createPasswordEncoder()
        );
        Member member = memberService.register(MemberFixture.createMemberRegisterRequest());

        assertThat(member.getId()).isNotNull();
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    // Mock
    @Test
    void registerTestMock() {
        EmailSenderMock emailSenderMock = new EmailSenderMock();

        MemberService memberService = new MemberService(
                new MemberRepositoryStub(), emailSenderMock, MemberFixture.createPasswordEncoder()
        );
        Member member = memberService.register(MemberFixture.createMemberRegisterRequest());

        assertThat(emailSenderMock.getTos()).hasSize(1);    // 한번 호출이 됬냐?
        assertThat(emailSenderMock.getTos().getFirst()).isEqualTo(member.getEmail());   // 얘가 메일 보낸 대상이 설정한 이메일이 맞나?

    }

    // Mockito
    @Test
    void registerTestMockito() {
        EmailSender emailSenderMock = Mockito.mock(EmailSender.class);
        MemberService memberService = new MemberService(
                new MemberRepositoryStub(), emailSenderMock, MemberFixture.createPasswordEncoder()
        );
        Member member = memberService.register(MemberFixture.createMemberRegisterRequest());

        Mockito.verify(emailSenderMock).send(eq(member.getEmail()), any(), any());
    }

    static class MemberRepositoryStub implements MemberRepository {
        @Override
        public Member save(Member member) {
            // reflection
            ReflectionTestUtils.setField(member, "id", 1L);
            ReflectionTestUtils.setField(member, "email", new Email("addr@naver.com"));

            return null;
        }

        @Override
        public Optional<Member> findByEmail(Email email) {
            return Optional.empty();
        }
    }

    static class EmailSenderStub implements EmailSender {
        @Override
        public void send(Email email, String subject, String body) {
        }
    }

    @Getter
    static class EmailSenderMock implements EmailSender {
        List<Email> tos = new ArrayList<>();

        @Override
        public void send(Email email, String subject, String body) {
            tos.add(email);
        }
    }


}