package apycazo.codex.auth.common;

import javax.servlet.http.HttpSession;

public class SessionUtil {

  private static final String SSO_STATUS_KEY = "sso-status";
  private static final String SSO_STATUS_VALUE_STARTED = "started";
  private static final String SSO_STATUS_VALUE_COMPLETED = "completed";
  private static final String SSO_IS_AUTHENTICATED_KEY = "is-authenticated";
  private static final String SSO_IS_AUTHENTICATED_VALUE_TRUE = "true";
  private static final String SSO_IS_AUTHENTICATED_VALUE_FALSE = "false";

  public static void startSso(HttpSession session) {
    session.setAttribute(SSO_STATUS_KEY, SSO_STATUS_VALUE_STARTED);
    session.setAttribute(SSO_IS_AUTHENTICATED_KEY, SSO_IS_AUTHENTICATED_VALUE_FALSE);
  }

  public static void completeSso(HttpSession session) {
    session.setAttribute(SSO_STATUS_KEY, SSO_STATUS_VALUE_COMPLETED);
    session.setAttribute(SSO_IS_AUTHENTICATED_KEY, SSO_IS_AUTHENTICATED_VALUE_TRUE);
  }

  public static void failSso(HttpSession session) {
    session.setAttribute(SSO_STATUS_KEY, SSO_STATUS_VALUE_COMPLETED);
    session.setAttribute(SSO_IS_AUTHENTICATED_KEY, SSO_IS_AUTHENTICATED_VALUE_FALSE);
  }

  public static boolean isAuthenticatedSession(HttpSession session) {
    return SSO_STATUS_VALUE_COMPLETED.equals(session.getAttribute(SSO_STATUS_KEY))
      && SSO_IS_AUTHENTICATED_VALUE_TRUE.equals(session.getAttribute(SSO_IS_AUTHENTICATED_KEY));
  }

  public static void clearAuthInfo(HttpSession session) {
    session.removeAttribute(SSO_STATUS_KEY);
    session.removeAttribute(SSO_IS_AUTHENTICATED_KEY);
  }

}
