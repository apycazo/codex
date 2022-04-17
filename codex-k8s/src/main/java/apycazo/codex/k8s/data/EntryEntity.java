package apycazo.codex.k8s.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "entry")
public class EntryEntity {

  @Id // note that 'key' is a reserved word
  @Column(name = "k", unique = true, length = 100)
  private String key;

  @Column(name = "v")
  private String value;
}
