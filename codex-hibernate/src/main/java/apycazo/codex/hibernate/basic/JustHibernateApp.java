package apycazo.codex.hibernate.basic;

import apycazo.codex.hibernate.common.BasicUserEntity;
import apycazo.codex.hibernate.common.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

@Slf4j
public class JustHibernateApp {

  public static void main(String[] args) {
    log.info("Configuring hibernate");
    HibernateUtil hibernate = new HibernateUtil();
    String mysqlUrl = "jdbc:mysql://localhost:3306/codex_hibernate";
    Configuration configuration = hibernate.configuration(mysqlUrl, "root", "root");
    configuration.addAnnotatedClass(BasicUserEntity.class); // Important!
    SessionFactory sessionFactory = hibernate.sessionFactory(configuration);
    BasicUserDao dao = new BasicUserDao(sessionFactory);
    // --- create a new user
    log.info("Creating new user");
    BasicUserEntity user = new BasicUserEntity();
    user.setActive(false);
    user.setUsername("john");
    int id = dao.create(user);
    log.info("Created user with id: {}", id);
    // --- list users
    log.info("Listing users");
    List<BasicUserEntity> list = dao.list();
    log.info("User count: {}", list.size());
    list.forEach(u -> log.info("User: {}", u));
  }
}
