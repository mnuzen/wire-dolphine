package dao;

import static org.junit.Assert.assertEquals;

import com.google.netpcapanalysis.dao.GeolocationDaoImpl;
import com.google.netpcapanalysis.dao.ReverseDNSLookupDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.ReverseDNSLookupDao;
import org.junit.Before;
import org.junit.Test;

public class ReverseDNSLookupDaoImplTest {
  public static ReverseDNSLookupDao dns;

  public void checkDns(String url, String expected) {
    assertEquals(expected, dns.lookup(url).record);
  }

  @Before
  public void setup() {
    dns = new ReverseDNSLookupDaoImpl();
  }

  @Test
  public void testGoogle() {
    checkDns("172.217.14.206", "sea30s01-in-f14.1e100.net");
  }

  @Test
  public void testReddit() {
    checkDns("72.247.244.88", "a72-247-244-88.deploy.static.akamaitechnologies.com");
  }

  @Test
  public void testImgur() {
    checkDns("173.231.140.219", "hostmaster.voxel.net");
  }

  @Test
  public void testYoutube() {
    checkDns("74.125.65.91", "91.65.125.74.bc.googleusercontent.com");
  }

  @Test
  public void testFacebook() {
    checkDns("157.240.11.35", "edge-star-mini-shv-02-lax3.facebook.com");
  }

  @Test
  public void testYahoo() {
    checkDns("98.137.149.56", "UNKNOWN-98-137-149-X.yahoo.com");
  }

  @Test
  public void testWikipedia() {
    checkDns("198.35.26.96", "text-lb.ulsfo.wikimedia.org");
  }
  @Test
  public void testNYTimes() {
    checkDns("151.101.65.164", "dns.ripe.net");
  }
  @Test
  public void testHackerNews() {
    checkDns("205.251.192.225", "ns-225.awsdns-28.com");
  }

  @Test
  public void testYCombinator() {
    checkDns("13.224.29.89", "server-13-224-29-89.sea19.r.cloudfront.net");
  }
}
