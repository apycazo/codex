package apycazo.codex.auth.saml2;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.authn.AuthnRequest;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.settings.IdPMetadataParser;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;
import com.onelogin.saml2.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class Saml2Service {

  private final Saml2Settings settings;
  private final boolean signatureIsRequired;

  public Saml2Service(
    @Value("${auth.saml.config.path:saml2.properties}") String filename,
    @Value("${saml.idp.config.metadata.url:}") String idpMetadataUrlString,
    @Value("${auth.saml.config.post.signed:false}") boolean signatureIsRequired
  ) throws Exception {
    SettingsBuilder settingsBuilder = new SettingsBuilder();
    if (StringUtils.hasLength(filename)) {
      settingsBuilder.fromFile(filename);
    }
    if (StringUtils.hasLength(idpMetadataUrlString)) {
      URL idpMetadataUrl = new URL(idpMetadataUrlString);
      Map<String, Object> idpConfig = IdPMetadataParser.parseRemoteXML(idpMetadataUrl);
      settingsBuilder.fromValues(idpConfig).build();
    }
    this.settings = settingsBuilder.build();
    this.signatureIsRequired = signatureIsRequired;
    // validate metadata
    List<String> errors = Saml2Settings.validateMetadata(getSPMetadata());
    if (errors.isEmpty()) {
      log.info("SAML metadata from '{}' has no errors", filename);
    } else {
      log.warn("SAML metadata from '{}' contains errors", filename);
      errors.forEach(error -> log.warn("SAML config error: {}", error));
    }
  }

  public String getSPMetadata() throws CertificateEncodingException {
    return settings.getSPMetadata();
  }

  public Response redirectBinding(String returnUrl, HttpServletRequest req, HttpServletResponse res)
    throws SettingsException, IOException {
    Auth auth = new Auth(settings, req, res);
    String redirectUrl = auth.login(returnUrl, false, false, true, false);
    try {
      URI uri = new URI(redirectUrl);
      return Response.temporaryRedirect(uri).build();
    } catch (URISyntaxException e) {
      log.warn("Invalid URI target '{}'", redirectUrl);
      return Response.serverError().build();
    }
  }

  public Response processPostBinding() throws IOException {
    AuthnRequest request = new AuthnRequest(settings, false, false, true);
    String samlRequest = request.getEncodedAuthnRequest();
    String base64decodedAndInflated = Util.base64decodedInflated(samlRequest);
    String xml = getSignedRequestIfRequired(base64decodedAndInflated, settings.getSignatureAlgorithm());
    String base64Request = Util.base64encoder(xml);
    URL ssoUri = settings.getIdpSingleSignOnServiceUrl();
    log.info("SAML POST Binding generated SAMLRequest={} for {}", base64Request, ssoUri);
    String htmlTemplate =
      "<HTML><HEAD><TITLE>SAML HTTP Post Binding</TITLE></HEAD>\n" +
        "<BODY Onload=\"document.forms[0].submit()\">\n" +
        "  <FORM METHOD=\"POST\" ACTION=\"%s\">\n" +
        "    <INPUT TYPE=\"HIDDEN\" NAME=\"SAMLRequest\" VALUE=\"%s\"/>\n" +
        "    <NOSCRIPT><P>JavaScript disabled, click to continue.</P>\n" +
        "      <INPUT TYPE=\"SUBMIT\" VALUE=\"CONTINUE\" />\n" +
        "    </NOSCRIPT>\n" +
        "  </FORM>\n" +
        "</BODY></HTML>";
    String form = String.format(htmlTemplate, ssoUri, base64Request);
    return Response.status(200)
      .entity(form)
      .type(MediaType.TEXT_HTML_VALUE)
      .build();
  }

  /**
   * Takes the provided xml string with the generated request, and injects the
   * signature if all values are present. On errors, it will log the result and
   * return the value as it was, to make sure a misconfiguration does not break
   * the authentication.
   * @param xmlString the generated xml as a string.
   * @param signAlgorithm the signature algorithm to use, taken from the current
   *                      Saml2Configuration instance.
   * @return the xml with the signed content, or the provided value otherwise.
   */
  private String getSignedRequestIfRequired(String xmlString, String signAlgorithm) {
    if (signatureIsRequired) {
      Document metadataDoc = Util.loadXML(xmlString);
      PrivateKey key = settings.getSPkey();
      X509Certificate cert = settings.getIdpx509cert();
      try {
        return Util.addSign(metadataDoc, key, cert, signAlgorithm)
          .replaceAll("&#xD;", "")
          .replaceAll("\n", "");
      } catch (Exception e) {
        log.error("Unable to include signature", e);
        return xmlString;
      }
    } else {
      return xmlString;
    }
  }

  public boolean processLoginResponse(HttpServletRequest req, HttpServletResponse res) {
    try {
      Auth auth = new Auth(settings, req, res);
      auth.processResponse();
      if (auth.isAuthenticated()) {
        log.info("Session is authenticated");
        return true;
      } else {
        log.warn("Response is not authenticated");
      }
    } catch (Exception e) {
      log.warn("Failed to process response", e);
    }
    return false;
  }

}
