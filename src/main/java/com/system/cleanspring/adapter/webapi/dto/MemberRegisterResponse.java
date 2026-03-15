package com.system.cleanspring.adapter.webapi.dto;

import com.system.cleanspring.domain.member.Member;

public record MemberRegisterResponse(
        Long memberId,
        String email

) {
    public static MemberRegisterResponse of(Member member) {
        return new MemberRegisterResponse(member.getId(), member.getEmail().address());
    }
}
