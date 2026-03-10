package com.system.cleanspring.application.member.provided;

import com.system.cleanspring.domain.member.Member;

/*
 * 회원을 조회한다.
 */
public interface MemberFinder {
    Member find(Long id);
}
