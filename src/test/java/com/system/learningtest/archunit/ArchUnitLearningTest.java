package com.system.learningtest.archunit;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

@AnalyzeClasses(packages = "tobyspring.learningtest.archunit")
public class ArchUnitLearningTest {
    /*
     * Application 클래스를 의존하는 클래스는 application, adapter에만 존재해야한다.
     */

    @ArchTest
    void app()
}
