# JPA와 도메인 모델 패턴


## JPA 모델과 도메인 모델이 다른가?

- 헥사고날 아키텍처 오해 = "도메인 모델을 JPA로 만든느 데이터 모델과 분리를 해야한다" <br>
  ▶ 근데 도메인 모델로 만든 엔티티를 JPA 엔티티로도 사용하기로했는데, 왜 그런 선택을 했는지, 프로젝트에 적용한 도메인 모델 패턴에 잘 적용하게 하는 방법은 무엇인지
- 우선 DB 매핑 데이터 모델을 어댑터로 따로 빼야한다는 관점에 대해서 이해해보자.

### "도메인 모델과 DB와 매핑되는 데이터 모델 분리하는 것이 필요한가??"
```markdown
1) 접근 방법
   - 어댑터 계층에 JPA 엔티티 등의 모델을 따로 만들고
   - Repository를 구현한 어댑터를 이용해서 도메인 오브젝트와 JPA 오브젝트를 매핑해준다
   - Member외에 MemberEntity 클래스를 만들어 JPA 관련 설정은 모두 이곳으로 옳긴다
   - MemberRepository를 구현한 MemberRepositoryJpaAdapter에서 이 두가지 오브젝트를 서로 매핑해 주는 코드를 작성한다.
  
2) 이런 접근 방법을 선호하는 이유
   1. 데이터 모델과 도메인 모델이 너무 다른 경우
       - 레거시 DB에 도메인 모델 설계를 적용하는 경우
   2. 복잡한 도메인 모델이 데이터 모델과 간단히 매핑되지 않는 경우
      - JPA 모델과는 다른 도메인 모델이 존재한다면
      - 예를 들어 데이터를 읽어와서 도메인 오브젝트로 복잡한 구조로 적용을 해 놓고 거기에 로직을 적용해서 결과를 얻어내야하는 이런류, <br>
        그 때에는 작업한 결과를 또 DB에 저장하진 않는다 이럴때 도메인 모델로 맵핑해주는 작업을 어댑터 같은 데서 해주는게 필요
   3. 데이터 저장 기술이 바뀌는 경우 
      - RDB에서 NoSQL로 가겠어 등등 
      - 근데 이건 Spring Data 프로젝트를 사용하면 간단한 작업이면 해결된다.
      - 즉 이건 Spring data 프로젝트 존재 이유
   4. 코드에 등장하는 JPA 애노테이션은 기술 의존적이니까
      - 그럼 기술 의존적이다라고하는 건? = "내가 그 기술을 안쓰면 작성한 코드는 아무 쓸모가 없어" 
      - 근데, 도메인 모델을 설계하고 JPA 엔티티로 전환하는 과정해서 생각해보자, 도메인 모델을 테스트했던 기능이 바뀐거 있나?
      - 없다. 그럼 적어도 도메인 로직은 JPA라는 기술에 의존적인게 아니다.
      - 근데 나중에 JPA 기술을 안쓰고 다른 방식으로 쓰겠다라고 하면 어떻게 해야하지?
      - 그럼 JPA 기술적인 코드를 바꿔야하는데 이는 annotation이 그게 문제가 생기나? 항상 JPA 라이브러리 파일을 포함해서 배포를 해야하나?
      - 아니다, annotation은 주석이다. 이는 코드 실행에 영향이 없다. 이들은 특정 프레임워크를 이용할 때 참고할 뿐이다. 
      - 그럼 결국엔 JPA 기술 의존적이니까 도메인 모델과 섞어서 쓰면 안된다. 라는 말이 와닿진 않는다. 
   5. 도메인 코드에 관심사가 다른 JPA 매핑 애노테이션, DB정보가 들어가야하니까.

3) 그럼 어느 상황에 JPA 엔티티를 어댑터로 따로 빼야하는데? 
   - (○) `2)-1.레거시 DB에 도메인 모델 설계를 적용` 방식
     - 이때는 필수적이게 해야한다.
   - (○) `2)-2. 복잡한 도메인 모델이 데이터 모델과 간단히 매핑되지 않는 경우` 방식
     - 이 경우는 일부에 한해서 필요한 경우가 있다.(일단 일대일 매핑이 아니기에)
   - (X) `2)-3. 데이터 저장 기술이 바뀌는 경우`? 
     - Spring Data project의 존재 이유이다.
   - (X) `2)-4. 코드에 등장하는 JPA 애노테이션은 기술 의존적이니까`의 관점?
     - annotation은 코드에 실행에 영향을 주지 않는다.

## JPA 기술의 정체성
   - JPA의 정체성 : JPA의 기술적 목표는 자바 애플리케이션 개발자가 **관계형 데이터베이스**를 관리하기 위해 **자바 도메인 모델을 활용**할 수 있는 객체/관계 매핑 기능을 제공하는 것
   - JPA Entity : "경량 영속 도메인 오브젝트"
   - ORM : 패러다임이 다른 **관계형 DB와 객체지향 모델의 불일치를 해결**하는 기술 (자바의 ORM 표준)

## 도메인 모델 패턴이란?
   - 단순 도메인 모델은 테이블과 클래스가 1:1로 매핑
   - 복잡한 도메인 모델은 DB 매핑이 어렵다는 문제가 있다. <br> 
     → DB 매핑이 어렵다. <br>
     → 이를 해결해주는게 JPA(ORM)기술
```
### JPA(ORM)이 매핑을 통해서 해결하려는 패러다임 불일치 문제
1. 세분성(Granularity) 불일치 
    - DB하고 클래스하고 1:1 매핑이 아니라, DB 테이블 하나가 한 개 이상의 클래스로 쪼개져있는 경우
    - 예를 들어 Email이라는 VO를 만들어서 Member 속성으로 쓰는 경우 여기서부터 1:1 매핑이 깨지는 것.
2. 상속(Subtype) 불일치
    - DB는 상속개념이 없음
3. 정체성(Identity) 불일치
    - 자바에선 PK 제한성이 없음
4. 연관(Association) 불일치
    - 자바에선 PK, FK 연결성이 없음
5. 데이터 탐색(Navigation) 불일치
    - 자바는 오브젝트를 타고 네비게이션이 가능한 반면 DB에선 그런 컨센이 없음

- 위 불일치한 패러다임을 JPA기술을 통해 해결이 가능.
- 즉, "복잡한 도메인 모델은 DB 매핑이 어렵다" 라는 문제를 ORM 기술이 해결하려고 하는 것과 동일

### Spring Data JPA 프로젝트
- 다양한 데이터 저장소(관계형 또는 비관계형 DB, 클라우드 기반 데이터 서비스)에 대한 데이터 접근을 단순하고 일관된 프로그래밍 모델로 제공
- 일관된 프로그래밍 모델: 저장소의 종류와 관계없이 동일한 방식으로 데이터에 접근토록함
- 보일러 플레이트 코드 감소
- 데이터 저장소의 특성 유지
- 확장성과 유연성
  - Repository<T, ID> : T 엔티티 이름 ID 루트 타입
  - T : 도메인 타입 = 엔티티 = aggregate root

### "도메인 모델과 JPA 모델을 반드시 분리해야한다는 주장에 대한 반박"
```markdown
1. 대부분 데이터 모델과 도메인 모델이 다르지 않음
2. 복잡한 도메인 모델의 매핑은 JPA가 충분히 지원
3. 모델 변환 로직과 유사한 두 가지 클래스로 인한 불필요한 복잡성 증가
   - 모델이 바뀌면 어댑터, 도메인 둘다 고쳐야하는데, 사람은 실수를 한다.
4. JPA는 근본적으로 도메인 오브젝트의 매핑을 위해서 설계된 기술이다.
5. JPA는 도메인 계층을 침범하지 않음. 코드를 변경하지 않는다.
6. 복잡한 쿼리 로직은 커스텀 리포지토리와 어댑터 구현을 통해서 개발 가능
    - (○) `2)-2. 복잡한 도메인 모델이 데이터 모델과 간단히 매핑되지 않는 경우` 방식
7. 도메인 계층과 데이터 계층의 결합은 불가피 하다.
```
---

#### 그럼 도메인 관심사가 아닌 각족 JPA 매핑 애노테이션은 어떻게 제거할 방법이 없을까? 
▶ 엔티티 클래스와 JPA 매핑 정보 분리

# JPA에서 DB를 준비하는 두가지 방법
## 1. 테이블을 직접 생성
 - SQL에서 직접 생성 
 - Flyway Migration Tool
## 2. Hibernate Schema Auto Generation
 - DB용 SQL를 따로 준비하지 않고, JPA Entity에다가 매핑과 관련된 정보를 넣으면 Hibernate가 자동 편집
   - 초기 개발이나 테스트에서는 편하지만
   - 운영 환경까지 끌고 가는건 위험

### 2. Hibernate Schema Auto Generation 활용
- `jpa.hibernate.ddl-auto: update` : 엔티티 도메인 변경에 따라서 자동으로 테이블이 alter 등이 된다. 
- 변경 쿼리를 확인하고 싶으면 
- DB > 해당 테이블 우클릭 > SQL Script > Generate DDL to Query Console
- 또는 단축키 Ctrl + shift + alt + b
- 확인해보면, 설정에 따라 create, alter 문이 생성이 되긴하는데. 정확하게 되진 않음. 그때마다 DB를 드랍했다가 다시 app을 실행시켜줘야한다.
- 그 이유로 로컬에서 사용하기는 위험함.
- constraint이름은 랜덤으로 설정되는데, 이는 버그가 일어나면 확인하기 어려움 그래서 이것도 annotation으로 설정해두면 변경할 수 있다. 
```java
@Table(name="MEMBER", uniqueConstraints =
@UniqueConstraint(name="UK_MEMBER_EMAIL_ADDRESS", columnNames = "email_address")
)
```

# 이제 도메인에 JPA 관심사를 분리해보자 = XML 이용
- 원래 JPA 데이터베이스와 맵핑, 모든 JPA와 관련된 설정은 XML 이란 걸 사용해서 하도록 만들어짐

## XML은 Annotation 설정을  override 한다
- 기본틀은 이렇게
```xml
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="http://www.hibernate.org/xsd/orm/mapping"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://www.hibernate.org/xsd/orm/mapping
                                     https://hibernate.org/xsd/orm/mapping/mapping-3.1.0.xsd">
    <access>FIELD</access>


</entity-mappings>
```
- 특히 이부분은 AI copilot이 잘 작성을 해준다 
> "이 Member안에 있는 annotation를 가지고 orm.xml에 들어갈 <entity>를 만들어줘"
- XML로 JPA 관심사를 분리했다 해도, 중요한 정보는 주석처리 하듯annotation으로 남겨두는게 좋다
> entity, NaturalId, OneToOne, ManyToOne 등 도메인 모델 관점에서 중요한 것.

# equal() toString()를 어떻게 관리?
- 엔티티 : 식별자기준 
  - natural ID : 변동성 있음
  - Primary ID : 변동성 없음 → 이걸로

## @EqualsAndHashCode()
  - jpa buddy 플러그인 설치
  - 해당 플러그인이 만들어주는 코드 사용 
    - 1. `@EqualsAndHashCode` 달고
      2. 알트 엔터 누르면 jpa buddy가 코드를 만들어줌
  - 여기서 참고 (`@Getter(onMethod_ = {@NullMarked})`)
    > package-info.java 에 @NullMarked 를 선언해 두었기 때문에, 기본적으로 getter 반환값은 null 이 아니라고 간주된다. <br>
    > 하지만 JPA 엔티티의 id 는 영속화 이전에는 실제로 null 일 수 있으므로, getId() 는 예외적으로 nullable 하다는 사실을 명시해 주어야 한다. <br>
    > 그래서 AbstractEntity 에서는 id 기반 equals/hashCode 를 공통 구현하면서, null id 상황과 Hibernate Proxy 비교까지 안전하게 처리한다.
- 즉, SuperClass에 이와같이 달아두면 된다.
```java
@MappedSuperclass
public abstract class AbstractEntity {
    @Id
    @Getter(onMethod_ = {@NullMarked})
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 여기에 equals, hashCode 오버라이드
}
```