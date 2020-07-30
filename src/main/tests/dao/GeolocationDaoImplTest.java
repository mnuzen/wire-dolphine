package dao;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.Test;

public class GeolocationDaoTest {

  @Setu
  public void setup() {

  }

  @Test
  public void invalidAddressReturnsUnknown() throws UnknownHostException {
    InetAddress test = InetAddress.getByName("127.0.0.1");
  }
}
