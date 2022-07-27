package apycazo.codex.java.basic;

import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class IpRange {

  /**
   * Checks the ip range. This can be used with jersey using
   * <pre>{@code
   *     HttpServletRequest httpReq = (HttpServletRequest) requestContext.getRequest();
   *     String remoteAddr = httpReq.getRemoteAddr();
   *     new IpRange().isInRange(remoteAddr);
   * }</pre>
   * @param args
   */
  public static void main(String[] args) {
    IpRange ipRange = new IpRange();
    ipRange.test("192.168.10.0", "192.168.10.128", "192.168.10.200");
    ipRange.test("192.168.10.127", "192.168.10.128", "192.168.10.200");
    ipRange.test("192.168.10.128", "192.168.10.128", "192.168.10.200");
    ipRange.test("192.168.10.129", "192.168.10.128", "192.168.10.200");
    ipRange.test("192.168.10.199", "192.168.10.128", "192.168.10.200");
    ipRange.test("192.168.10.200", "192.168.10.128", "192.168.10.200");
    ipRange.test("192.168.10.201", "192.168.10.128", "192.168.10.200");
    ipRange.test("192.168.10.10", "192.168.10.10", "192.168.10.15");
    ipRange.test("192.168.10.15", "192.168.10.10", "192.168.10.15");
    ipRange.test("192.168.10.50", "192.168.0.0", "192.168.255.255");
    ipRange.test("192.168.9.50", "192.168.0.0", "192.168.255.255");
    ipRange.test("192.168.11.50", "192.168.0.0", "192.168.255.255");
  }

  private void test(String ip, String lower, String upper) {
    try {
      log.info("{} ∈ [{}..{}] = {}", ip, lower, upper, isInRange(ip, lower, upper));
    } catch (UnknownHostException e) {
      log.warn("Failed to process", e);
    }
  }

  private boolean isInRange(String target, String lower, String upper) throws UnknownHostException {
    InetAddress ipTarget = Inet4Address.getByName(target);
    InetAddress ipLower = Inet4Address.getByName(lower);
    InetAddress ipUpper = Inet4Address.getByName(upper);
    return isInRange(ipTarget, ipLower, ipUpper);
  }

  private boolean isInRange(InetAddress target, InetAddress lower, InetAddress upper) {
    long targetValue = inetAddressToInt(target);
    long lowerValue = inetAddressToInt(lower);
    long upperValue = inetAddressToInt(upper);
    return targetValue <= upperValue && targetValue >= lowerValue;
  }

  private long inetAddressToInt(InetAddress address) {
    long resultIP = 0;
    byte[] ipAddressOctets = address.getAddress();

    for (byte octet : ipAddressOctets) {
      resultIP <<= 8;
      resultIP |= octet & 0xFF;
    }
    return resultIP;
  }
}
