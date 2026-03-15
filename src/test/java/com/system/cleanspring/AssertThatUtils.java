package com.system.cleanspring;

import com.system.cleanspring.domain.member.Member;
import org.assertj.core.api.AssertProvider;
import org.assertj.core.api.Assertions;
import org.springframework.test.json.JsonPathValueAssert;

import java.util.function.Consumer;

public class AssertThatUtils {

    public static Consumer<AssertProvider<JsonPathValueAssert>> isEqualTo(Member request) {
        return value -> Assertions.assertThat(value).isEqualTo(request.getEmail());
    }

    public static Consumer<AssertProvider<JsonPathValueAssert>> isNotNull() {
        return value -> Assertions.assertThat(value).isNotNull();
    }
}
