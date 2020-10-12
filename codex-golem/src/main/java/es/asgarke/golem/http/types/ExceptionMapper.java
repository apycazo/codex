package es.asgarke.golem.http.types;

public interface ExceptionMapper<T extends Throwable> {

  boolean dealsWith(Throwable e);
  Response getResponse(T e);
}
