package apycazo.codex.hibernate.demo;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Transactional // since this is a generic: Write operations are not allowed in read-only mode.
@SuppressWarnings("unchecked")
public class GenericRepository<ENTITY, ID extends Serializable> {

  private final Class<ENTITY> entityClass;
  private final Class<ID> idClass;

  @Autowired
  protected HibernateTemplate hibernate;

  public GenericRepository() {
    super();
    Class<?>[] classes = GenericTypeResolver.resolveTypeArguments(getClass(), GenericRepository.class);
    if (classes == null || classes.length != 2) {
      throw new RuntimeException("Unable to resolve generic types from: " + getClass().getName());
    } else {
      entityClass = (Class<ENTITY>) classes[0];
      idClass = (Class<ID>) classes[1];
      log.info("Entity class: {}, Id class: {}", entityClass.getName(), idClass.getName());
    }
  }

  public ID save(ENTITY entity) {
    return (ID)hibernate.save(entity);
  }

  public void saveOrUpdate(ENTITY entity) {
    hibernate.saveOrUpdate(entity);
  }

  @Transactional(readOnly = true)
  public List<ENTITY> find() {
    return hibernate.loadAll(entityClass);
  }

  @Transactional(readOnly = true)
  public ENTITY findById(ID id) {
    DetachedCriteria criteria = DetachedCriteria
      .forClass(entityClass)
      .add(Property.forName("id").eq(id));
    return (ENTITY) hibernate.findByCriteria(criteria).stream().findFirst().orElse(null);
  }

  public void remove(ENTITY entity) {
    hibernate.delete(entity);
  }
}
