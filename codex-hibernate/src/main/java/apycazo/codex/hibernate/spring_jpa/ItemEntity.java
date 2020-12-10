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
}
