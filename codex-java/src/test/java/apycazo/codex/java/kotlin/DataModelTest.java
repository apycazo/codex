package apycazo.codex.java.kotlin;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(JUnitPlatform.class)
public class DataModelTest {

  @Test
  void createJavaPojoFromJava() {
    DataModel data = new DataModel("john", 30);
    assertNotNull(data);
    assertEquals("john", data.getName());
    assertEquals(30, data.getValue());
  }
}
