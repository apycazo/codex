package es.asgarke.golem.tools;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitPlatform.class)
class ParserToolTest {

  @Test
  void test_int_parser() {
    String text = "10";
    int value = ParserTool.readInt(text).orElse(0);
    assertThat(value).isEqualTo(10);

    Object[] arr = new Object[1];
    arr[0] = value;
  }

}