package com.system.cleanspring.application;

import com.system.cleanspring.application.provided.MemberRegister;
import com.system.cleanspring.application.required.EmailSender;
import com.system.cleanspring.application.required.MemberRepository;
import com.system.cleanspring.domain.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/*
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class MemberService implements MemberRegister {

    private final MemberRepository memberRepository;
    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member register(MemberRegisterRequest memberRegisterRequest) {

        checkDuplicateEmail(memberRegisterRequest);


        Member member = Member.register(memberRegisterRequest, passwordEncoder);

        memberRepository.save(member);

        sendWelcomeEmail(member);

        return member;
    }

    private void sendWelcomeEmail(Member member) {
        emailSender.send(member.getEmail(), "등록", "등록완료필요");
    }

    private void checkDuplicateEmail(MemberRegisterRequest memberRegisterRequest) {
        memberRepository.findByEmail(new Email(memberRegisterRequest.email())).ifPresent(e -> {
            throw new DuplicatedEmailException("이미 사용중인 이메일");
        });
    }
}
