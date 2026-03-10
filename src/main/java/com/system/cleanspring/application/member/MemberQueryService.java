package com.system.cleanspring.application.member;

import com.system.cleanspring.application.member.provided.MemberFinder;
import com.system.cleanspring.application.member.required.MemberRepository;
import com.system.cleanspring.domain.member.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class MemberQueryService implements MemberFinder {

    private final MemberRepository memberRepository;

    @Override
    public Member find(Long id) {
        return  memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. id:" + id));
    }
}
