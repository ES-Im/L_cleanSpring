# 1. JDK
### java development kit : 자바를 가지고 개발하는데 필요한 것들을 모아 둔 것
 - JRE을 포함한다.
   - java runtime environment : 자바를 실행하는 환경
   - 자바로 만든 프로그램을 컴파일해서 클래스 파일을 만든 다음 jar로 만든 다음 실행할 때 필요한 환경을 구성하는 프로그램을 모아둔 것을 일컬음
 - javac(컴파일러) : jvm에서 실행할 수 있는 실행 파일로 만들어줌
 - tool
### 개발환경에서 JDK를 설치하는 방법
    1. IDE의 JDK 다운로드 기능
    2. JDK 배포판에서 설치
    3. JDK 버전을 관리하는 도구를 이용해서 설치
        - SDKMAN, jabba, Asdf, jvms
        - SDK에서 각 개발팀에서 해당 app을 어떤 버전을 이용해 개발했는지 체크할 수 있고, 버전관리가 용이


# 2. IDE / IntelliJ
#### Integrated Development Environment 

# 3. HTTPie
 - Http 프로토콜을 이용해서  API를 호출하고 응답을 받을 때 사용할 수 있는 옵션

# 4. MySQL

# 5. Docker
 - 컨테이너 방식으로 환경 등을 구성할 수 있게 만드는 툴

# 6. AI 개발 지원 도구
 - GitHub Copilot (IntelliJ Plugin, github.com)
   - 코드 completion,LLM 기반한 코드 피드백 
 - ChatGPT
 - Claude
 - Perplexity
 - Cursor, VSCode+Copilot
 - DeepSeek .... 

<hr>

# 개발 환경 구성 
## build 언어를 코틀린으로 두는 이유?
> - “태스크 이런 것들이 결국은 다 그냥 코틀린 언어로 만들어진 코드거든요”
> - “그 안에서 파라미터를 확인한다던가 설정 옵션을 본다던가… 훨씬 빠르고 편리합니다”
> - “프로젝트가 커지고 복잡해지면 Gradle 빌드 스크립트를 개선하거나…”
> - “직접 스크립트나 플러그인을 개발할 때… 코틀린을 사용하시는 것이 훨씬 유리합니다”

| 기준                 | Groovy DSL   | Kotlin DSL  |
| ------------------ | ------------ | ----------- |
| IDE 자동완성/타입 안정성    | 상대적으로 약함     | 강함(정적 타입)   |
| 설정 옵션 탐색/리팩토링      | 불편할 수 있음     | 편하고 안전      |
| 프로젝트 규모 커질 때 유지보수  | 점점 어려워질 수 있음 | 구조화에 유리     |
| 커스텀 플러그인/빌드 로직 코드화 | 가능           | 더 자연스럽고 안정적 |

## 의존성 구성
```kotlin
// spring data jpa
implementation("org.springframework.boot:spring-boot-starter-data-jpa")
// lombok
implementation("org.projectlombok:lombok")
// db
runtimeOnly("com.h2database:h2")
runtimeOnly("com.mysql:mysql-connector-j")
// docker compose
runtimeOnly("org.springframework.boot:spring-boot-docker-compose")
```
- docker compose support 
  - 기존 : 도커에 설치해놓은 서비스들의 연결 정보 등을 일일이 프로퍼티에 입력
  - 사용후 : connection details라는 기술을 이용해서 도커에 띄운 DB를 포함 여러가지 서비스들에 대한 연결정보를 자동으로 프로퍼티에 추가해줌
  - 즉, application.yml 파일에 도커 연결 설정을 빼도 compose.yml파일 보고 알아서 매칭시켜주는 것
  - 처음에 알아 만들어준 compose.yml 파일은 동적 포트로 설정된다. 그래서 호스트포트도 지정해주어야함
  - docker 컨테이너 확인 : service > 원하는 이미지 우클릭 > inspection
- Spring data JPA
  - 그냥 JPA와 다름, Spring Data JPA.

## 유용한 plugin
- LangCursor : 현재 모드가 한글이면 커서 색깔을 빨간색으로 만들어줌
- Mermaid : 텍스트기반으로 다이어그램을 쉽게 그려주게 함
- SpotBugs

## 인텔리제이가 제공하는 completion 기능
 - editor > general > code completion / inline completion