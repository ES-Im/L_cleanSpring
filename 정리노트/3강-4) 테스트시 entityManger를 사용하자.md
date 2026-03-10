# 테스트에서 EntityManager를 사용하는 이유
- 관련 테스트 : `MemberRegisterTest.java` 내 `activate()` 테스트
- JPA 기반 테스트에서는 영속성 컨텍스트(1차 캐시)의 동작 때문에 실제 데이터베이스 동작이 제대로 검증되지 않는 경우가 발생할 수 있다. 
- 이러한 문제를 방지하고 실제 데이터베이스 흐름을 검증하기 위해 테스트 코드에서 EntityManager.flush()와 EntityManager.clear()를 활용

## 우선 entityManager 기능을 살펴보자
| 기능        | 설명                                        |
| --------- | ----------------------------------------- |
| `flush()` | 영속성 컨텍스트에 있는 변경사항을 **DB에 SQL로 반영**        |
| `clear()` | 영속성 컨텍스트를 **비워서 모든 엔티티를 detached 상태로 만듦** |
즉,
- flush() → 메모리 → DB 동기화
- clear() → 1차 캐시 제거


### 1) Entity Manager없이 테스트할 때의 문제점
```java
@Test
void activate() {
    Member member = memberRegister
            .register(MemberFixture.createMemberRegisterRequest());

    member = memberRegister.activate(member.getId());

    assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
}
```
이경우 실행되는 쿼리
```sql
select ...
insert into member ...
```
- 이 경우, 이미 "같은 트랜잭션"에서 같은 Entity가 "영속성 컨텍스트"에 있기 때문에, 
- activate() 내부에서 호출하는 `memberRepository.findById()`가 동작하지 않음.
- 즉 이건 `Repository → DB → 결과 검증`이 아닌 `영속성 컨텍스트[1차 캐시 조회] → 그대로 반환`가 된다.


### 2) 그래서 flush/clear를 사용하면? 
```java
@Test
void activate() {
    Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
    **entityManager.flush();**
    **entityManager.clear();**

    member = memberRegister.activate(member.getId());

    **entityManager.flush();**

    assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
}
```

```sql
 select ... // register 과정 이미 존재하는 회원인지 '조회'
 insert ... // register DB에 반영
 select ... // activate 과정 기존 회원인지 '조회'
 update ... // activate DB에 반영
```
- 즉, `회원 저장 → DB 반영 → DB에서 조회 → 도메인 로직 실행 → DB 업데이트`
- 영속성 컨텍스트(1차 캐시)를 제거하여 Repository 조회가 실제 DB 조회가 되도록 만들고, <br>
  도메인 로직의 변경이 실제 DB에 반영되는지 검증할 수 있다.

### 결론
> JPA 테스트에서 예상보다 쿼리가 적게 실행되거나 테스트가 지나치게 쉽게 통과하는 경우, <br> 
> 이는 _**영속성 컨텍스트의 1차 캐시**_ 때문일 가능성이 높다. <br> 
> 이럴 때 flush()와 clear()를 적절한 위치에 추가하면 실제 데이터베이스 기준으로 테스트를 검증할 수 있다.
