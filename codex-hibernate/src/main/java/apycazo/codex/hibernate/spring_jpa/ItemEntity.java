package apycazo.codex.hibernate.spring_jpa;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "items")
public class ItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String name;
  private int value;

  public static ItemEntity withValues(String name, int value) {
    ItemEntity itemEntity = new ItemEntity();
    itemEntity.setName(name);
    itemEntity.setValue(value);
    return itemEntity;
  }
}
