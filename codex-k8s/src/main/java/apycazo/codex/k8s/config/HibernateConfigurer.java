package apycazo.codex.k8s.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateConfigurer {

  public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
  public static final String DIALECT = "org.hibernate.dialect.MySQL5Dialect";

  public Configuration configuration(String url, String usr, String pwd) {
    return configuration(DRIVER, DIALECT, url, usr, pwd);
  }

  public Properties properties(String driver, String dialect, String url, String usr, String pwd) {
    Properties properties = new Properties();
    properties.put(AvailableSettings.DRIVER, driver);
    properties.put(AvailableSettings.DIALECT, dialect);
    properties.put(AvailableSettings.URL, url);
    properties.put(AvailableSettings.USER, usr);
    properties.put(AvailableSettings.PASS, pwd);
    properties.put(AvailableSettings.SHOW_SQL, "true");
    properties.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");
    properties.put(AvailableSettings.HBM2DDL_AUTO, "create-drop");
    return properties;
  }

  public Configuration configuration(String driver, String dialect, String url,
    String usr, String pwd) {
    Properties properties = properties(driver, dialect, url, usr, pwd);
    Configuration configuration = new Configuration();
    configuration.setProperties(properties);
    // After this, register classes with: configuration.addAnnotatedClass(...)
    return configuration;
  }

  public SessionFactory sessionFactory(Configuration configuration) {
    ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
      .applySettings(configuration.getProperties()).build();
    return configuration.buildSessionFactory(serviceRegistry);
  }
}
