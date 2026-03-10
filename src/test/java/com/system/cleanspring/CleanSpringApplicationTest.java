package com.system.cleanspring;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.*;

class CleanSpringApplicationTest {

    @Test
    void run() {
        try(MockedStatic<SpringApplication> mock = Mockito.mockStatic(SpringApplication.class)) {

            CleanSpringApplication.main(new String[0]);

            mock.verify(() -> SpringApplication.run(CleanSpringApplication.class, new String[0]));
        }
    }

}