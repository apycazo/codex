package apycazo.codex.hibernate.common;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateUtil {

  public static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";
  public static final String DEFAULT_DIALECT = "org.hibernate.dialect.MySQL5Dialect";

  public Configuration configuration(String url, String usr, String pwd) {
    return configuration(DEFAULT_DRIVER, DEFAULT_DIALECT, url, usr, pwd);
  }

  public Configuration configuration(String driver, String dialect, String url, String usr, String pwd) {
    Properties properties = new Properties();
    properties.put(Environment.DRIVER, driver);
    properties.put(Environment.DIALECT, dialect);
    properties.put(Environment.URL, url);
    properties.put(Environment.USER, usr);
    properties.put(Environment.PASS, pwd);
    properties.put(Environment.SHOW_SQL, "true");
    properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
    properties.put(Environment.HBM2DDL_AUTO, "create-drop");
    Configuration configuration = new Configuration();
    configuration.setProperties(properties);
    // configuration.addAnnotatedClass(...)
    return configuration;
  }

  public SessionFactory sessionFactory(Configuration configuration) {
    ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
      .applySettings(configuration.getProperties()).build();
    return configuration.buildSessionFactory(serviceRegistry);
  }
}
