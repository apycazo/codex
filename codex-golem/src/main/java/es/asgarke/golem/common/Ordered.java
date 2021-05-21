package es.asgarke.golem.common;

public interface Ordered {

  int highest = 0;
  int lowest = Integer.MAX_VALUE;

  /**
   * Returns the order to apply to this class. The lower the value, the higher the priority.
   * @return the order value to use.
   */
  Integer getOrder();
}
