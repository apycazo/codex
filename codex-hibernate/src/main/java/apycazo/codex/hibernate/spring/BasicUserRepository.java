package apycazo.codex.hibernate.spring;

import apycazo.codex.hibernate.common.BasicUserEntity;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Repository
@Transactional
public class BasicUserRepository extends HibernateDaoSupport {

  public BasicUserRepository(SessionFactory sessionFactory) {
    super.setSessionFactory(sessionFactory);
  }

  public void persist(BasicUserEntity entity) {
    HibernateTemplate hibernateTemplate = getHibernateTemplate();
    if (hibernateTemplate != null) {
      hibernateTemplate.saveOrUpdate(entity);
    } else {
      log.error("No hibernate template found");
    }
  }

  @Transactional(readOnly = true)
  public List<BasicUserEntity> find() {
    return hibernate().loadAll(BasicUserEntity.class);
  }

  public void remove(BasicUserEntity entity) {
    hibernate().delete(entity);
  }

  public List<BasicUserEntity> findJohn() {
    DetachedCriteria criteria = DetachedCriteria
      .forClass(BasicUserEntity.class)
      .add(Property.forName("username").eq("john"));
    return query(criteria);
  }

  public List<BasicUserEntity> findActive() {
    DetachedCriteria criteria = DetachedCriteria
      .forClass(BasicUserEntity.class)
      .add(Restrictions.eq("active", true));
    return query(criteria);
  }

  @SuppressWarnings("unchecked")
  private List<BasicUserEntity> query(DetachedCriteria criteria) {
    return (List<BasicUserEntity>) hibernate().findByCriteria(criteria);
  }

  private HibernateTemplate hibernate() {
    HibernateTemplate hibernateTemplate = getHibernateTemplate();
    if (hibernateTemplate == null) {
      throw new RuntimeException("No hibernate template found");
    } else {
      return hibernateTemplate;
    }
  }
}
