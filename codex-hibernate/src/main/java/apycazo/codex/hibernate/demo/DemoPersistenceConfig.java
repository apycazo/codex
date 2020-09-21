package apycazo.codex.hibernate.demo;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan
@PropertySource("classpath:h2.properties")
public class DemoPersistenceConfig {

  @Bean(destroyMethod = "close")
  public DataSource dataSource(
    @Value("${db.driver}") String driver, @Value("${db.url}") String url,
    @Value("${db.usr}") String usr, @Value("${db.pwd}") String pwd) {
    BasicDataSource datasource = new BasicDataSource();
    datasource.setDriverClassName(driver);
    datasource.setUrl(url);
    datasource.setUsername(usr);
    datasource.setPassword(pwd);
    return datasource;
  }

  @Bean
  public LocalSessionFactoryBean sessionFactory(
    DataSource dataSource,
    @Value("${hibernate.show_sql:false}") String showSql,
    @Value("${hibernate.hbm2ddl.auto:create-drop}") String auto,
    @Value("${hibernate.dialect:org.hibernate.dialect.MySQL5Dialect}") String dialect) {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    sessionFactory.setPackagesToScan(DemoPersistenceConfig.class.getPackageName());
    Properties properties = new Properties();
    properties.put(Environment.DIALECT, dialect);
    properties.put(Environment.HBM2DDL_AUTO, auto);
    properties.put(Environment.SHOW_SQL, showSql);
    sessionFactory.setHibernateProperties(properties);
    return sessionFactory;
  }

  @Bean // another option: DataSourceTransactionManager
  public HibernateTransactionManager transactionManager(LocalSessionFactoryBean sessionFactory) {
    HibernateTransactionManager txManager = new HibernateTransactionManager();
    txManager.setSessionFactory(sessionFactory.getObject());
    return txManager;
  }

  @Bean
  public HibernateTemplate hibernateTemplate(SessionFactory sessionFactory) {
    return new HibernateTemplate(sessionFactory);
  }
}
