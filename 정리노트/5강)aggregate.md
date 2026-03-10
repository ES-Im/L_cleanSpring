# 애그리거트를 이용한 일괄성 있는 모델 설계
- Member -(참조)→ MemberDetail 경우 MemberModifyService 안에 5가지 작업이 이뤄진다.
  1. Member.register
  2. MemberDetail.create
  3. link
  4. MemberRepository.save
  5. MemberDetail.save
- 이렇게 한다면, 실질상 같은 도메인이지만, 작업을 할때마다 각각의 엔티티를 따로 작업을 해야한다.
- 둘은 분리된 엔티티지만, 하나의 쪼개지지 않는 유닛으로 본다면?

### → Member Aggregate root로
- 외부에서 Aggregate에 접근하거나 변경할때는 Aggregate root인 Member만 참조하는 방식
- 다시 register 과정을 생각한다면
  1. Member.register 호출하되 이 안에서 memberDetail도 등록
  2. MemberRepository.save에서 이 aggregate를 한번에 등록
     - Repository에 엔티티를 넣는거 아니냐? → "정확히 말하면 이 엔티티는 aggregate root 엔티티이다."

# Aggregate
## 정의
- DDD에 소개된 도메인 모델 구성 요소/패턴의 하나
- **데이터 변경**의 목적을 위해 하나의 단위로 취급되는 연관된 객체들의 클러스터
- 루트(root, 외부에 노출)와 루트를 중심으로 경계(boundary)를 가진다.
  - 경계 내부 : 엔티티 + 값 객체가 하나 또는 여러 개가 존재
  - 애그리거트 루트는 내부에 포함된 "단일 엔터티"

## 특징
- 데이터 변경 시 하나의 단위로 취급: 데이터 변경의 일관성 유지
- 루트를 통한 접근 제어 : 외부 객체는 루트 엔티티에만 참조를 가짐
- 데이터 일관성 유지
  - 경계 내의 어떤 변경 사항이 있을 때 전체 aggregate의 모든 불변식(invariant, a+b=total이 항상 유지)이 충족되어야 한다.
  - aggregate를 넘어서는 불변식은 이벤트나 배치 등을 통해 특정 시간 내에 해결할 수 있다.
  - ex) Member 등록시 MemberDetails의 RegisteredAt가 동시에 등록되야 한다
  - **검색 및 접근 방식** : 리포지토리를 통해서 애그리거트 루트만 직접 얻을 수 있다.
    - 내부 엔티티는 루트로부터 연관관계를 통해서 접근한다.
  - 생명주기 관리 캡슐화: Factory와 Repository를 이용해서 객체들의 생명주기에 걸쳐 체계적이고 의미있는 단위로 조작

## 목표
- 일관성 유지 : 객체 그룹에 적용되는 불변식을 유지하는 수단
- 이해 용이성: 객체의 시작과 끝을 명확히 해서 모델을 더 쉽게 이해
- 트랜잭션 및 동시성 관리: 트랜잭션 범위롸 데이터 일관성 유지 방법 제공
- 모델 단순화: 연관관계 탐색을 제한하고 루트를 통해서만 접근하도록 해준다
- Factory와 Repository가 복잡한 생명주기 전환을 캡슐화하는 단위가 되도록 한다

## 적용방법
- JPA의 cascading을 적절하게 활용한다.
  - cascading : 한 엔티티 변경이 일어나고 영속화 하면 연결된 엔티티에 동일한 영속화를 한다.
- 리포지토리는 "애그리거트 단위"로 만든다
  - spring data JPA의 핵심 원칙
  - Repository<T,ID>: T = Aggregate Root
  - 리포지토리 리턴 타입은 애그리거트 루트
- (권장) 가능하다면 하나의 트랜잭션에 하나의 애그리거트만 변경한다
  - (필수) 하나의 애그리거트를 두개 이상의 트랜잭션으로 쪼개면 안된다.
- 다른 애그리거트의 참조는 "애그리거트 루트"에 대해서만 한다
  - 연관관계 애그리거트 루트의 레퍼런스 대신 루트의 ID 값만 저장하기도 한다.
  - (오해) 애그리거트를 참조할 때 aggregate root에 대한 reference를 직접 가지지말고 루트 엔티티의 id 값만 저장해야한다.
  - 대부분의 애그리거트 간 관계는 ID 참조가 기본이긴 하지만, 상황에 따라 객체 참조도 허용
  - 루트의 ID값을 할지, 객체 참조를 할지는 상황에 따라 결정해야한다.

## 설계와 적용의 어려움
- 적정한 애그리거트 경계 선택하는게 어려움
  - Aggregate 내부 객체만 뽑아야할 경우 이 경계가 맞나 의심할 때 있음
  - → 그래서 개발을 진행하면서 이 범위도 계속 달라지기도 함
  - → 대체로 작은 애그리거트가 될 가능성이 높음
- 전체 회원의 이메일을 뽑아서 전체 공지 발송??
  - 이 경우 MemberDetail까지 뽑아와서(필요없는 엔티티 탐색) 성능 비효율
  - → 대체로 lazy loading의 도움을 받으면 된다.
- 내부 엔티티로의ㅡ 직접 접근이나 여러 애그리거트를 한번에 조회하는 기능이 필요한 경우가 있다
  - Aggregate 내부 객체 조회 / 변경 등 같은 경우 root로만 접근하면 비효율 
  - → 이런 경우 너무 고민하지말고 유연성있게 
- 도메인 이벤트와 최종적 일관성(eventual consistency)의 사용이 요구된다.
  - 최종적 일관성 : 약간의 시간의 차이는 있지만, 결국에는 지켜지는 일관성
- 완벽한 애그리거트가 아니어도 괜찮다
- 어디까지나 도메인 주도 개발이 아니라, DDD에 있는거 중에 괜찮은거 커스텀 하는 것

## 헥사고날 아키텍처와 애그리거트
- 애그리거트 단위로 애플리케이션(헥사곤)을 구성하는 방법이 유용하다.
  - application에 port기능을 두고 여기를 통해서만 내부 기능에 접근 제한을 두는 걸 aggregate 단위로 설계
  - 즉, 내가 다른 aggregate 기능을 사용해서 뭘 변경해야 한다 싶으면 provided Interface를 타고 요청을 보내는 거
- 다른 애그리거트로의 접근은 애플리케이션 포트를 통해서 **ID를 전달**하는 방식으로(설령 객체 참조를 가지고 있더라도) 
  - 애플리케이션 내부 repository에서 root entity를 조회하는 방식으로 이뤄지게 강제할 수 있다.
  - 같은 트랜잭션 안이라면 영속 컨텍스트 안에 엔티티를 cache해두는데 id값을 갖고 조회하면 DB까지 조회하지 않아도 된다.
  - 설령 aggregate 간 요청이 다른 트랜잭션에서 나뉘더라도, JPA 2차 캐시를 쓰는 등 JPA 영속 컨텍스트가 유지되게 만드는 방법을 쓰면 된다.
- 도메인 이벤트와 리스터를 이용해서 애그리거트 사이의 작업을 연결할 수 있다
  - 이벤트에 필요한 애그리거트 루트 ID를 전달한다
- 애그리거트를 설계하고 각각을 독립적인 애플리케이션으로 분리 

# 예제
`Member(root) → MemberDetail`
1. Domain
```java
public class Member {

    @OneToOne(cascade =  CascadeType.ALL)
    private MemberDetail detail;
    
    public static Member register() {
        Member member = new Member();

        member.detail = MemberDetail.create();

        return member;
    }
}

static MemberDetail create() {  // → root만 접근할수있도록 제한
    MemberDetail de = new MemberDetail();
    // ....
    return de;
}
```
※ 참고로 @OneToOne에서 다른 걸 설정하지 않으면 eager fetch로 실행되므로 Lazy로 해야하면 변경

2. Port는 root만
- Member쪽만 save()를 구현
- 그럼 MemberDetail은 언제 영속화 되느냐
  - Repository를 따로 만들지 않기때문에, cascade를 이용
