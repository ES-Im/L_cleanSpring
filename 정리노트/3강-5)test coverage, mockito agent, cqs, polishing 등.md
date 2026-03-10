# 조회 로직과 변경 로직을 분리하자
- 두 로직은 시간이 지나면 의존하는 오브젝트들이 서로 종류가 달라짐
- MemberService → MemberModifyService(변경로직), MemberQueryService(조회로직) 등
- 이는 CQS(Command Query Separation)과 연관되며, 마틴은 Command대신 modify 용어를 추천
`CQS(Command Query Separation)는 메서드를 실행 시점에 부수 효과를 일으키는 Command와 값을 반환하지만 부수 효과가 없는 Query로 구분하고 분리하는 원칙입니다. 서비스 분리 등에 응용될 수 있습니다.`

# 테스트 커버리지
> test Dir 우클 > More Run/Debug > Run 'Tests in 'project.....' with Coverage
> 최소 50 ~ 60% 이상은 맞추는 것이 좋다.

# 코드 폴리싱
 - 기능이 잘 동작하는가? : 테스트 
 - 정적 테스트 : spotbugsTest 
 - 불필요한 import 삭제 : `code > Optimize Imports` : ctrl + alt + o

# `@FallBack`
같은 타입 후보들 중에서 우선적으로 선택되는 대상은 아니고, <br>
일반 후보(non-fallback)들이 없을 때 대신 선택될 수 있는 빈

| 애노테이션        | 역할                        | 느낌          |
| ------------ | ------------------------- | ----------- |
| `@Primary`   | 같은 타입 후보가 여러 개일 때 기본 선택   | 우선순위 높은 기본값 |
| `@Qualifier` | 같은 타입 후보 중 특정 빈을 명시적으로 지정 | 이름 찍어서 선택   |
| `@Fallback`  | 일반 후보가 없을 때 대체 후보로 사용     | 최후의 대안      |

# `testImplementation("org.junit-pioneer:junit-pioneer:2.3.0")`
- junit 관련 익스텐션을 모아둔 라이브러리
- jdk 25 버전과는 현재 호환이 되지 않는다. 사용하려면 그 이전의 LST 버전 사용해야할듯
```java
@Test
@StdIo
void sendEmail(StdOut out) {
    DummyEmailSender dummyEmailSender = new DummyEmailSender();

    dummyEmailSender.send(new Email("toby@splearn.app"), "subject", "body");

    assertThat(out.capturedLines()[0]).isEqualTo("");
}
```

# Mockito Agent 추가 
```kotlin
val mockitoAgent: Configuration = configurations.create("mockitoAgent")

dependencies {
    mockitoAgent("org.mockito:mockito-core:5.18.0") { isTransitive = false }
}

tasks.withType<Test> {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}
```
## 왜 추가하냐?
- 최신 JDK의 보안/에이전트 정책 변화 등 정책이 엄격해짐
- 예전에는 Mockito가 테스트 도중 알아서 agent를 붙이거나 우회적으로 처리한게 최근 환경에서는 명시적으로 javaagent를 붙이는 방식이 안전해진 것
- 그래서 아예 테스트 JVM을 띄울 때: `-javaagent:...`ㅡ을 옵션으로 Mockito를 붙여서 실행

# Mockito의 static 테스트는 try 로 종료되게 명시
