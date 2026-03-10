package com.system.cleanspring.application.member.required;

import com.system.cleanspring.domain.member.Profile;
import com.system.cleanspring.domain.shared.Email;
import com.system.cleanspring.domain.member.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/*
 * 회원 정보를 저장하거나 조회한다.
 */
public interface MemberRepository extends Repository<Member, Long> {

    Member save(Member member);

    Optional<Member> findByEmail(Email email);

    Optional<Member> findById(Long id);

    @Query("select m from Member m where m.detail.profile = :profile")
    Optional<Object> findByProfile(Profile profile);
}

