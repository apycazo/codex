package apycazo.codex.hibernate.spring;

import apycazo.codex.hibernate.common.BasicUserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpringDemoService {

  private final BasicUserRepository repository;

  public void runDemo() {
    // --- create a new user
    log.info("Creating new user");
    BasicUserEntity user = new BasicUserEntity();
    user.setActive(false);
    user.setUsername("john");
    repository.persist(user);
    log.info("Created user");
    // --- list users
    log.info("Listing users");
    List<BasicUserEntity> list = repository.find();
    log.info("User count: {}", list.size());
    list.forEach(u -> log.info("User: {}", u));
    // find john
    List<BasicUserEntity> john = repository.findJohn();
    log.info("John? {}", john);
    // delete and recreate with examples
    repository.remove(john.get(0));
    BasicUserEntity.examples().forEach(repository::persist);
    // find active users
    List<BasicUserEntity> activeUsers = repository.findActive();
    activeUsers.forEach(u -> log.info("User: {}", u));
  }
}
