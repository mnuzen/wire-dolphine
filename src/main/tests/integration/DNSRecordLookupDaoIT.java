package integration;

import static org.junit.Assert.assertEquals;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.netpcapanalysis.dao.ReverseDNSLookupDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.ReverseDNSLookupDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DNSRecordLookupDaoIT {
  public static ReverseDNSLookupDao dns;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());



  @After
  public void tearDown() {
    helper.tearDown();
  }

  public void checkDns(String url, String expected) {
    assertEquals(expected, dns.lookup(url).getDomain());
  }

  @Before
  public void setup() {
    helper.setUp();
    dns = new ReverseDNSLookupDaoImpl();
  }

  @Test
  public void testGoogle() {
    checkDns("172.217.14.206", "1e100.net");
  }

  @Test
  public void testReddit() {
    checkDns("72.247.244.88", "akamaitechnologies.com");
  }

  @Test
  public void testImgur() {
    checkDns("173.231.140.219", "voxel.net");
  }

  @Test
  public void testYoutube() {
    checkDns("74.125.65.91", "googleusercontent.com");
  }

  @Test
  public void testFacebook() {
    checkDns("157.240.11.35", "facebook.com");
  }

  @Test
  public void testYahoo() {
    checkDns("98.137.149.56", "yahoo.com");
  }

  @Test
  public void testWikipedia() {
    checkDns("198.35.26.96", "wikimedia.org");
  }
  @Test
  public void testNYTimes() {
    checkDns("151.101.65.164", "ripe.net");
  }
  @Test
  public void testHackerNews() {
    checkDns("205.251.192.225", "awsdns-28.com");
  }

  @Test
  public void testYCombinator() {
    checkDns("13.224.29.89", "cloudfront.net");
  }

  @Test
  public void ipv6Google() {
    checkDns("2607:f8b0:4005:80a::200e", "verisign-grs.com");
  }
}
