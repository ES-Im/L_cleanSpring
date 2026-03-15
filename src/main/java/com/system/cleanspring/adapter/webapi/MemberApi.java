package com.system.cleanspring.adapter.webapi;

import com.system.cleanspring.adapter.webapi.dto.MemberRegisterResponse;
import com.system.cleanspring.application.member.provided.MemberRegister;
import com.system.cleanspring.domain.member.Member;
import com.system.cleanspring.domain.member.MemberRegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApi {

    private final MemberRegister memberRegister;

    @PostMapping("/api/members")
    public MemberRegisterResponse register(@RequestBody @Valid MemberRegisterRequest request) {
        Member member = memberRegister.register(request);

        return MemberRegisterResponse.of(member);
    }
}
