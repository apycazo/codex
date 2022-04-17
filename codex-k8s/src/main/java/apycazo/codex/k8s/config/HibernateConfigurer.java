package apycazo.codex.k8s.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Optional;
import java.util.Properties;

public class HibernateConfigurer {

  public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
  public static final String DIALECT = "org.hibernate.dialect.MySQL5Dialect";

  public Configuration configuration(String url, String usr, String pwd, Properties properties) {
    return configuration(DRIVER, DIALECT, url, usr, pwd, properties);
  }

  public Configuration configuration(String driver, String dialect, String url,
    String usr, String pwd, Properties hibernateProperties) {
    Properties properties = properties(driver, dialect, url, usr, pwd, hibernateProperties);
    Configuration configuration = new Configuration();
    configuration.setProperties(properties);
    // After this, register classes with: configuration.addAnnotatedClass(...)
    return configuration;
  }

  public Properties properties(String driver, String dialect, String url, String usr, String pwd, Properties props) {
    Properties properties = Optional.ofNullable(props).orElse(new Properties());
    properties.put(AvailableSettings.URL, url);
    properties.put(AvailableSettings.USER, usr);
    properties.put(AvailableSettings.PASS, pwd);
    properties.putIfAbsent(AvailableSettings.DRIVER, driver);
    properties.putIfAbsent(AvailableSettings.DIALECT, dialect);
    properties.putIfAbsent(AvailableSettings.SHOW_SQL, "false");
    properties.putIfAbsent(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");
    properties.putIfAbsent(AvailableSettings.HBM2DDL_AUTO, "create-drop");
    return properties;
  }

  public SessionFactory sessionFactory(Configuration configuration) {
    ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
      .applySettings(configuration.getProperties()).build();
    return configuration.buildSessionFactory(serviceRegistry);
  }
}
