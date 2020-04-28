package apycazo.codex.java.basic;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

@Slf4j
public class ListNetworkInterfaces {

  public static void main(String[] args) throws Exception {
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
    for (NetworkInterface itf : Collections.list(interfaces)) {
      if (itf.isUp()) {
        log.info("Interface name: '{}', display name: '{}'", itf.getName(), itf.getDisplayName());
        Enumeration<InetAddress> inetAddresses = itf.getInetAddresses();
        Collections.list(inetAddresses).forEach(inetAddress -> log.info("  Address: '{}'", inetAddress));
      }
    }
  }
}
