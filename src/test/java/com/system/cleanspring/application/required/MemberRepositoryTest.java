package com.system.cleanspring.application.required;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


@DataJpaTest
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired
    EntityManager em;




}