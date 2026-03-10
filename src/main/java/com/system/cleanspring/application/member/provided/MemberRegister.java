package com.system.cleanspring.application.member.provided;

import com.system.cleanspring.domain.member.Member;
import com.system.cleanspring.domain.member.MemberInfoUpdateRequest;
import com.system.cleanspring.domain.member.MemberRegisterRequest;
import jakarta.validation.Valid;

/*
 * 회원의 등록과 관련된 기능을 제공
 */
public interface MemberRegister {
    Member register(@Valid MemberRegisterRequest memberRegisterRequest);

    Member activate(Long memberId);

    Member deactivate(long memberid);

    Member updateInfo(Long memberId, @Valid MemberInfoUpdateRequest memberInfoUpdateRequest);
}
