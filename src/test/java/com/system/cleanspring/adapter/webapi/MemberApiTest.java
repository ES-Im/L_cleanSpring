package com.system.cleanspring.adapter.webapi;


import com.system.cleanspring.adapter.webapi.dto.MemberRegisterResponse;
import com.system.cleanspring.application.member.required.MemberRepository;
import com.system.cleanspring.domain.member.Member;
import com.system.cleanspring.domain.member.MemberFixture;
import com.system.cleanspring.domain.member.MemberRegisterRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonPathValueAssert;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tools.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.function.Consumer;

import static com.system.cleanspring.AssertThatUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
public class MemberApiTest {
    final MockMvcTester mvcTester;
    final ObjectMapper objectMapper;
    final MemberRepository memberRepository;

    @Test
    void register() throws UnsupportedEncodingException {
        Member request = MemberFixture.createMember(1L);
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post().uri("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json).exchange();     // 응답값을 받아오기 위해 따로 객체로 분리

        assertThat(result)
                 .hasStatusOk()
                 .bodyJson()
                 .hasPathSatisfying("$.memberId", isNotNull())  // 자주사용하는 assert Util method는 util 클래스로 분리
                 .hasPathSatisfying("$.email", isEqualTo(request));

        MemberRegisterResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), MemberRegisterResponse.class);
        Member member = memberRepository.findById(response.memberId()).orElseThrow();

        assertThat(member.getEmail()).isEqualTo(request.getEmail());
    }

}
