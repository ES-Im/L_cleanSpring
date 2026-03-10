package com.system.cleanspring.domain.member;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {

    @Test
    void profile() {
        new Profile("tobyilee");
        new Profile("tony101");
        new Profile("13214");
    }

    @Test
    void profile_fail() {
        assertThatThrownBy(() ->
            new Profile("")
        ).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() ->
            new Profile("longlonglonglonglonglong")
        ).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() ->
            new Profile("프로필")
        ).isInstanceOf(IllegalArgumentException.class);
    }

}