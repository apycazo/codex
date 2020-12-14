package apycazo.codex.hibernate.spring_jpa;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Example using spring-data-jpa. This requires :
 * <li>org.hibernate:hibernate-entitymanager</li>
 * <li>mysql:mysql-connector-java</li>
 * <li>org.springframework.data:spring-data-jpa</li>
 * Note that spring-data-jpa uses hibernate under the hood.
 */
@Slf4j
@Configuration
@ComponentScan
@PropertySource("classpath:h2.properties")
@EnableTransactionManagement
@EnableJpaRepositories("apycazo.codex.hibernate.spring_jpa")
public class SpringJpaApp {

  public static void main(String[] args) {
    ConfigurableApplicationContext ctx = init();
    SpringJpaDemoService service = ctx.getBean(SpringJpaDemoService.class);
    service.runDemo();
  }

  public static ConfigurableApplicationContext init() {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(SpringJpaApp.class);
    ctx.refresh();
    return ctx;
  }

  @Bean(destroyMethod = "close")
  public DataSource dataSource(
    @Value("${db.driver}") String driver,
    @Value("${db.url}") String url,
    @Value("${db.usr}") String usr,
    @Value("${db.pwd}") String pwd) {
    BasicDataSource datasource = new BasicDataSource();
    datasource.setDriverClassName(driver);
    datasource.setUrl(url);
    datasource.setUsername(usr);
    datasource.setPassword(pwd);
    log.info("Configured datasource for {}", url);
    return datasource;
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
    DataSource dataSource,
    @Value("${hibernate.show_sql:false}") String showSql,
    @Value("${hibernate.hbm2ddl.auto:create-drop}") String auto,
    @Value("${hibernate.dialect:org.hibernate.dialect.MySQL5Dialect}") String dialect) {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    String packageToScan = SpringJpaApp.class.getPackageName();
    log.info("Set package to scan: {}", packageToScan);
    em.setDataSource(dataSource);
    em.setPackagesToScan(packageToScan);
    Properties properties = new Properties();
    properties.put(Environment.DIALECT, dialect);
    properties.put(Environment.HBM2DDL_AUTO, auto);
    properties.put(Environment.SHOW_SQL, showSql);
    JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    em.setJpaProperties(properties);

    return em;
  }

  @Bean
  public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
    return transactionManager;
  }
}
