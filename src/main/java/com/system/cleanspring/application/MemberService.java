package com.system.cleanspring.application;

import com.system.cleanspring.application.provided.MemberRegister;
import com.system.cleanspring.application.required.EmailSender;
import com.system.cleanspring.application.required.MemberRepository;
import com.system.cleanspring.domain.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

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

    @Override
    public Member activate(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. id:" + id));

        member.activate();

        return memberRepository.save(member);   // Spring Data JPA의 특징 
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
