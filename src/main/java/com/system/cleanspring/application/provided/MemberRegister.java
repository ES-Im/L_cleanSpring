package com.system.cleanspring.application.provided;

import com.system.cleanspring.domain.Member;
import com.system.cleanspring.domain.MemberRegisterRequest;
import jakarta.validation.Valid;

/*
 * 회원의 등록과 관련된 기능을 제공
 */
public interface MemberRegister {
    Member register(@Valid MemberRegisterRequest memberRegisterRequest);

    Member activate(Long memberId);
}
