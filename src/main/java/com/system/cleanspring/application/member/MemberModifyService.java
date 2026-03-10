package com.system.cleanspring.application.member;

import com.system.cleanspring.application.member.provided.MemberFinder;
import com.system.cleanspring.application.member.provided.MemberRegister;
import com.system.cleanspring.application.member.required.EmailSender;
import com.system.cleanspring.application.member.required.MemberRepository;
import com.system.cleanspring.domain.member.*;
import com.system.cleanspring.domain.shared.Email;
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
public class MemberModifyService implements MemberRegister {

    private final MemberFinder memberFinder;
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
        Member member = memberFinder.find(id);

        member.activate();

        return memberRepository.save(member);
    }

    private void sendWelcomeEmail(Member member) {
        emailSender.send(member
                .getEmail(), "등록", "등록완료필요");
    }

    private void checkDuplicateEmail(MemberRegisterRequest memberRegisterRequest) {
        memberRepository.findByEmail(new Email(memberRegisterRequest.email())).ifPresent(e -> {
            throw new DuplicatedEmailException("이미 사용중인 이메일");
        });
    }

    @Override
    public Member deactivate(long memberid) {
        Member member = memberFinder.find(memberid);
        member.deactivate();

        return memberRepository.save(member);
    }

    @Override
    public Member updateInfo(Long memberId, MemberInfoUpdateRequest memberInfoUpdateRequest) {
        Member member = memberFinder.find(memberId);

        checkDuplicateProfile(member, memberInfoUpdateRequest.profileAddress());

        member.updateInfo(memberInfoUpdateRequest);

        return memberRepository.save(member);
    }

    private void checkDuplicateProfile(Member member, String profileAddress) {
        if(profileAddress.isEmpty()) return;

        Profile currentProfile = member.getDetail().getProfile();
        if(currentProfile != null && currentProfile.address().equals(profileAddress)) return;

        if(memberRepository.findByProfile(new Profile(profileAddress)).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 프로필 주소입니다");
        }
    }
}
