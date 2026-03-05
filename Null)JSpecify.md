유투브 정리 : [SivaLabs-
Spring Boot 4 + jSpecify : Say Goodbye to NullPointerExceptions](https://youtu.be/p3v8wJG3-zo?si=l1dand3EO2xWcfro)

- [의존성](https://jspecify.dev/docs/using/)
---

## 왜 필요하나? = NPE 방지
```java
public class MyResp {

    static String getAddress() {

        return null;
    }
}

public class Main {

    static void main() {
        String name = null;
        System.out.println(name.length());  // NPE 경고 o

        String address = getAddress();
        System.out.println(address.length());   // NPE 경고 x
    }

    static String getAddress() {
        return MyResp.getAddress();
    }
}
```

---

## 사용법
- 패키지 단위로 @NullMarked를 사용하고, @Nullable, @NullUnmarked를 이용해서 예외를 줘라.

### 표준화된 널 애노테이션
> JSpecify는 크게 @Nullable, @NonNull, @NullMarked, @NullUnmarked라는 네 가지 키워드로 널 가능성을 표현한다.
> 1) @Nullable: 해당 타입이 널일 수 있음을 명시 (ex. @Nullable String)
> 2) @NonNull: 해당 타입이 절대 널이 아님을 명시 (ex. @NonNull String)
> 3) @NullMarked: 해당 범위(모듈, 패키지, 클래스, 메서드)에 선언된 타입들은 기본적으로 널이 아닐 것으로 간주
> 4) @NullUnmarked: 이미 @NullMarked가 선언된 범위 안에서 다시 널에 대한 지정 없음 상태로 되돌리기
> * 출처 : [승팡의 개발일지](https://seungpnag.tistory.com/103)

### `@NullMarked`
> 해당 범위(모듈, 패키지, 클래스, 메서드)에 선언된 타입들은 기본적으로 널이 아닐 것으로 간주
- 일반적인 non-null, nullable annotation 사용방식과 유사하다, 즉 원하는 범위에 선언하여 널이 아닌 걸로 간주시키면 된다.

```java
// 패키지 단위로 Null경고 띄우라고 선언
@NullMarked
package com.example.demo;

import org.jspecify.annotations.NullMarked;
```
- 결과로 null이 리턴되는 라인에 `"null is returned by the method declared as @NullMarked"` 경고문을 표시한다.

### `@NullMarked`
> 이미 @NullMarked가 선언된 범위 안에서 다시 널에 대한 지정 없음 상태로 되돌리기
> 
```java
// 메서드, 클래스 등에 표시하여 @NulMarked로 선언된 범위 내에서 
// Null 경고를 띄우지 말라고 선언할 수 있다.
@NullUnmarked
static String getAddress() {

    return null;
}
```
### `@Nullable`
> 해당 타입이 널일 수 있음을 명시
```java
record Person(Long id, String name, @Nullable String address) {}
```

---

## 같이 쓰는 Dependencies
### 왜 같이 쓰냐? = 컴파일단계에서 놓친거 빌드단계에서 방어하려고.
> 빌드 로그에서 nullable로 추론된 expression을 null 체크 없이 dereference하고 있다는 오류가 발생한다. <br>
> 이는 컴파일러가 null-safety 규칙을 강제하고 있기 때문이며, <br>
> 해당 expression에 대해 if (xxx != null)와 같은 <br>
> 명시적인 null 체크를 추가하여 dereference가 안전함을 보장하면 빌드 오류를 해결할 수 있다.

### 그래서 어떤거 추가하냐? errorprone, nullaway
- "it will check if where are any such violations, if they are. let's fail the build."
- "so if it is optional if you add this at nullable annotation again it is going to show that okay
you are unboxing this that could potentially return null pointer exception"

※ @NullMarked(JSpecify)로 마킹된 패키지/클래스 범위에서만 위반을 빌드 에러
1) 플러그인 가져오기
```kotlin
plugins {
    java
    id("net.ltgt.errorprone") version "5.1.0"
    id("net.ltgt.nullaway") version "3.0.0"
}
```
2) import
```java
import net.ltgt.gradle.nullaway.nullaway
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.errorprone
```
3) dependencies
```java
dependencies {
    implementation("org.jspecify:jspecify:1.0.0")
    errorprone("com.google.errorprone:error_prone_core:2.48.0")
    errorprone("com.uber.nullaway:nullaway:0.13.1")
}
```
4) nullaway 범위 설정
```kotlin
nullaway {
    onlyNullMarked.set(true)
}
```
5) Gradle 빌드에서 모든 Java 컴파일 작업(JavaCompile)에 대해 Error Prone 설정을 적용하고, 그중 NullAway 검사 결과를 “경고”가 아니라 “에러”로 취급해서 빌드를 실패
```kotlin
tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        check("NullAway", CheckSeverity.ERROR)
    }
}
```

---

## tip
 - 컴파일 단계에서 warning 줄이 안보이면? 
 - setting > inspection > Nullability and data flow problems에서 'severity' 단계를 변경

---

## Type-use Annotation
* 출처 : [Null없이 스프링부트 사용하기](https://velog.io/@juhyeon1114/Spring-boot-4-Null-%EC%97%86%EC%9D%B4-%EC%8A%A4%ED%94%84%EB%A7%81-%EB%B6%80%ED%8A%B8-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-w.-JSpecify-NullAway)
- JSpecify가 제시하는 널 애노테이션(@Nullable, @NonNull)은 type-use 위치에서 적용할 수 있다.
- 이는 자바 8부터 도입된 Type Annotations”개념을 활용하는 것으로, 어떤 타입에 @Nullable을 붙이느냐에 따라 의미가 달라질 수 있다.

`@Nullable String[]`
 - “배열 요소(String)가 null일 수 있다”는 의미
 - 배열 객체 자체는 @NullMarked 하에서 NonNull로 간주됨
`String @Nullable []`
 - 배열 객체 자체가 null일 수 있다는 의미
 - 배열 안의 String 요소는 NonNull
`@Nullable String @Nullable []`
 - 배열 객체도 null 가능 + 배열 안의 요소도 null 가능
 - 가장 넓은 범위로 null을 허용

