package com.system.cleanspring.adapter.webapi;

import com.system.cleanspring.application.member.provided.MemberRegister;
import com.system.cleanspring.domain.member.MemberFixture;
import com.system.cleanspring.domain.member.MemberRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(MemberApi.class)
@RequiredArgsConstructor
class MemberApiWebTest {
    final MockMvcTester mvcTester;
    final ObjectMapper objectMapper;

    @MockitoBean
    private MemberRegister memberRegister;


    @Test
    void register() {
        when(memberRegister.register(any())).thenReturn(MemberFixture.createMember(1L));

        MemberRegisterRequest request = MemberFixture.createMemberRegisterRequest();
        String json = objectMapper.writeValueAsString(request);

        assertThat(
                mvcTester.post().uri("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).hasStatusOk()
         .bodyJson().extractingPath("$.memberId").asNumber().isEqualTo(1);  // Long 타입이 아닌이유 : jsonPath는 숫자 타입을 Long이 아닌 Integer로 인식하기 때문

        verify(memberRegister).register(request);   // 이 메서드가 호출됬는지 체크
    }

}