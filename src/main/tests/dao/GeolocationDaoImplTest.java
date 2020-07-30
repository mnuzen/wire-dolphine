package dao;

import static org.junit.Assert.assertEquals;

import com.google.netpcapanalysis.dao.GeolocationDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.GeolocationDao;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.Before;
import org.junit.Test;

public class GeolocationDaoImplTest {

  public static GeolocationDao geo;

  @Before
  public void setup() {
    geo = new GeolocationDaoImpl();
  }

  @Test
  public void invalidAddressReturnsUnknown() throws UnknownHostException {
    InetAddress test = InetAddress.getByName("127.0.0.1");
    assertEquals(geo.getCountry(test), "unknown");
  }

  @Test
  public void ipReturnsUS() throws UnknownHostException {
    InetAddress test = InetAddress.getByName("54.70.247.191");
    assertEquals(geo.getCountry(test), "United States");
  }

}
