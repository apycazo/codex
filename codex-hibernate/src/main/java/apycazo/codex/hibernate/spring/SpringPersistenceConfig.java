package apycazo.codex.hibernate.spring;

import apycazo.codex.hibernate.common.BasicUserEntity;
import apycazo.codex.hibernate.common.HibernateUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class SpringPersistenceConfig {

  @Bean(destroyMethod = "close")
  public DataSource dataSource(@Value("${db.driver}") String driver, @Value("${db.url}") String url,
                               @Value("${db.usr}") String usr, @Value("${db.pwd}") String pwd) {
    BasicDataSource datasource = new BasicDataSource();
    datasource.setDriverClassName(driver);
    datasource.setUrl(url);
    datasource.setUsername(usr);
    datasource.setPassword(pwd);
    return datasource;
  }

  @Bean
  public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    sessionFactory.setPackagesToScan(BasicUserEntity.class.getPackageName());
    sessionFactory.setHibernateProperties(new HibernateUtil().basicProperties());
    return sessionFactory;
  }

  @Bean // another option: DataSourceTransactionManager
  public HibernateTransactionManager transactionManager(LocalSessionFactoryBean sessionFactory) {
    HibernateTransactionManager txManager = new HibernateTransactionManager();
    txManager.setSessionFactory(sessionFactory.getObject());
    return txManager;
  }
}
