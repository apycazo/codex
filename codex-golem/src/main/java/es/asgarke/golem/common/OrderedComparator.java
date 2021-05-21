package es.asgarke.golem.common;

import java.util.Comparator;

public class OrderedComparator implements Comparator<Ordered> {

  @Override
  public int compare(Ordered a, Ordered b) {
    return a.getOrder().compareTo(b.getOrder());
  }
}
