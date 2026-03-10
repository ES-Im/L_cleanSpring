package com.system.cleanspring.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

public record Profile(
        String address
) {
    private static final Pattern PROFILE_ADDRESS_PATTERN =
            Pattern.compile("^[a-z0-9]+");

    public Profile {
        if(address == null || (!address.isEmpty() && !PROFILE_ADDRESS_PATTERN.matcher(address).matches())) {
            throw new IllegalArgumentException("주소는 영문 소문자와 숫자로만 구성되어야 합니다: " + address);
        }

        if (!PROFILE_ADDRESS_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("주소 형식이 바르지 않습니다: " + address);
        }

        if( address.length() > 15) throw new IllegalArgumentException("주소는 최대 15자리");
    }

    public String url() {
        return "@" + address;
    }
}
