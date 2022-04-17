package apycazo.codex.k8s.others;

public class DbError extends RuntimeException {

  public DbError(String msg) {
    super(msg);
  }

  public DbError(String msg, Throwable th) {
    super(msg, th);
  }
}
