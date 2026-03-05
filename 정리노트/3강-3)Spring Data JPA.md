## 출처 
 - [Active Record](https://www.martinfowler.com/eaaCatalog/activeRecord.html)이 아닌, ORM 패러다임을 구현하기 때문에 __JPA에는 save란 메서드는 없다.__ 
 - [save() 안티패턴관련 포스팅 - Vlad Mihalcea](https://vladmihalcea.com/best-spring-data-jparepository/)
 - [인프런 질의응답 : Spring data JPA - 왜 Save()를 쓰나?](https://www.inflearn.com/community/questions/1643563)

## JPA 스펙에는 persist()와 merger() 두 개뿐이다.
1) JPA의 persist()는 비영속 객체를 영속화 시키는 것
2) merge()는 여러가지 기능이 있지만 파라미터로 전달되는 엔티티의 상태가 무엇이든 상관없이 이를 영속 컨텍스트에 집어넣는 것이 목적
 - SQL 관점에서는 insert, update와 같은 관점에서 바라보지만, JPA 관점에서는 아예 다르게 "엔티티의 상태 관점"으로 본다.
 - 그래서 RDB/SQL으로 변환하는 과정에서 "추상화 단계"가 쌓이면서 사용하는 패턴이 생기는 것
---
## [Spring Data JPA]의 save()
 - CRUDRepository의 save()
```java
public void save(Member member) {
	em.persist(member);
}
```
- 매개변수로 전달된 객체가 곧 영속성 엔티티가 된다.
---
## 그래서 Spring Data JPA에서는 왜 save()를 쓰란건데?

### 1. 우선 save()동작에서 의아한점부터 체크하고, 왜 공식문서에서 쓰라고하는지 확인하자.
[Spring Data JPA의 레퍼런스 문서](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html)
```java
@Service
public class UserManagementImpl implements UserManagement {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public UserManagementImpl(UserRepository userRepository,
    RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

  @Transactional
  public void addRoleToAllUsers(String roleName) {

    Role role = roleRepository.findByName(roleName);    // → role도 영속상태일거고

    for (User user : userRepository.findAll()) {
      user.addRole(role);                               // → user도 연관관계 오너거나 양방향 관계라서 영속상태일텐데
      userRepository.save(user);                        // → 왜 merge를 호출하지?
    }
  }
}
```
- 불러온 객체가 영속상태로 올려져있을텐데, save를 호출하여 굳이, merge()로 영속상태로 올리라는 명령을 내린이유????
> Note that the call to save is not strictly necessary from a JPA point of view, <br>
> but should still be there in order __"to stay consistent to the repository abstraction"__ offered by Spring Data. <br>
> ▶ "즉, Spring Data가 제공하는 리포지토리 추상화에 대한 일관성을 지키기 위해서 save()는 여기 있어야 한다"
> 
- 그리고 영속상태라면 merge는 무시를 한다, 이 무시도 merge() 스펙에 나오는 기능
> "어짜피 merge()가 호출될 거고, merge()는 이미 영속 컨텍스트라면 그냥 무시합니다. 에러를 내지 않습니다."

### 2. 도메인 이벤트 사용한다고?
> Spring Data가 주는 놀라운 기능 중에서 도메인 이벤트를 순수하게 도메인 레벨에서 관리하게 하는 기능이 있습니다. <br>
> 엔티티에서 이벤트를 발행하면, 이게 결국 스프링 인벤트 시스템을 타고 동작하게 되죠. <br>
> 그래서 도메인 로직과 도메인 이벤트 발행이 분리되는 처참한 코드를 피할 수 있습니다. 매우 응집도가 높은 코드가 되죠.<br>
> 그런데 이런 __"Spring Data의 도메인 이벤트 기능을 사용할 때"__ 반드시 필요한 것이 최초 등록 뿐 아니라 <br>
> __"수정이 일어나는 경우에도 반드시 리포지토리의 save()를 호출해야 합니다."__ <br>
> save()가 실행되는 메카니즘을 이용해서 이벤트가 발행됐는지 체크하고 이를 처리하는 방식이 꼭 필요하기 때문입니다.

---
## 근데 save()가 안티패턴이라는건 뭐야? - [Vlad Mihalcea](https://vladmihalcea.com/best-spring-data-jparepository/)

## 우선 [JPARepository]의 save()부터 보자.
- SimpleJpaRepository의 save()
```java
@Override
@Transactional
public <S extends T> S save(S entity) {

    Assert.notNull(entity, ENTITY_MUST_NOT_BE_NULL);

    if (entityInformation.isNew(entity)) {
        entityManager.persist(entity);
        return entity;
    } else {
        return entityManager.merge(entity);
    }
}
```
1. save되는 엔티티가 새로 영속화되는 엔티티(비영속 엔티티)라면 em.persist()를 호출하여 영속화한다.
2. save되는 엔티티가 영속화된 전적이 있는 엔티티(준영속 엔티티, ID 값)라면 em.merge()를 호출하여 병합한다.
    - 2번 케이스의 경우 매개변수로 들어온 객체를 영속화하여 반환하는 것이 아니라 기존에 저장되어 있던 엔티티를 최신화하여 반환하는 것이 된다. <br>이 경우에는 아래와 같이 반환 객체를 사용해야만 정상적인 로직 수행이 가능해진다.<br>
      `Member member = memberRepository.save(memberRequest);`

### 그래서 뭐가 문젠데? 
- save의 호출은 관리되고있는 엔티티를 merge하면, MergeEvent가 트리거 되면서 CPU 사이클을 쓴다.
- save 메서드는 entity가 새로운 객체인지 판단 못할때도 있다. <br> 만약, 식별자가 하랑되어 있는 엔티티라면 Spring data JPA는 persist대신 merge를 호출해서 불필요한 조회 쿼리가 실행된다.
- 그니까 배치 돌릴때 문제가 될수있음.

```java
@Transactional
public void saveAntiPattern(Long postId, String postTitle) {
⠀
    Post post = postRepository.findById(postId).orElseThrow();
⠀
    post.setTitle(postTitle);       // → 이미 영속
⠀
    postRepository.save(post);      // → 여기서 mergeEvent로 select 쿼리가 불필요하게 발생할 수 있음.
}
```
> If this happens in the context of a batch processing task, then it’s even worse, you can generate lots of such useless SELECT queries.<br>
> The problem is the save line, which, while unnecessary, it’s not cost-free. Calling merge on a managed entity burns CPU cycles by triggering a MergeEvent, <br>
> which can be cascaded further down the entity hierarchy only to end up in a code block that does this:

---
## to-do 메모
솔직히 말해서, 아직 헷갈리는게 많아. 현 강의에서 도메인 이벤트가 포함된 spring data JPA를 찍먹해보고, 위 내용을 다시 확인하자.

그래서 나중에 정리할 to-do
 - 현재 merge가 영속 컨텍스트에 이미 엔티티가 올라가있으면 작동이 무시된다는 건 알고있어서 JPARepository를 사용하는 개인프로젝트에도 save()를 달긴했는데,
 - 안티패턴이라는 점에 놀라고, 심지어 배치 task에 조심하란것도 놀랍고....
 - 근데 또, spring data JPA에서는 기술 추상화를 위한 일관성을 위해서 쓰라는데, 결국 SimpleJpa랑 뿌리랑 기술 추상화 일관성 목표는 같을텐데 왜 다른 결과인지도 모르겠어... 헷갈리는게 많네 
 - 즉, Myasset 프로젝트를 spring data jpa로 변경해야할 건지(배치 쓸 일이 많은데 위 내용이 내가 이해한게 맞다면 JPARepository보다 spring data jpa를 쓰는게 더 이식성 좋아보임)
 - 아니면 required port부분 유지하되, save자체를 다 정리하는게 맞는지?  