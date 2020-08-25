package integration;

import static org.junit.Assert.assertEquals;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.netpcapanalysis.dao.ReverseDNSLookupDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.ReverseDNSLookupDao;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
  public void parallelTest() throws Exception {
    String location = getClass().getClassLoader().getResource("ips_test.txt").getFile();
    File ips = new File(location);
    BufferedReader br = new BufferedReader(new FileReader(ips));

    List<String> l = new ArrayList<>();
    int uniq = 0;
    String line;
    while ((line = br.readLine()) != null) {
      uniq++;
      String[] split = line.split("\\s");
      String ip = split[0];
      int num = Integer.parseInt(split[1]);
      for (int i = 0; i < num; i++) {
        l.add(ip);
      }
    }

    System.out.println("uniqs:" + uniq);
    Collections.shuffle(l);
    System.out.println("size of randomized ip list");
    System.out.println(l.size());

    dns.lookup(l);
  }

  @Test
  public void testGoogle() {
    checkDns("172.217.14.206", "1e100.net");
  }

  @Test
  public void testInvalid() {
    checkDns("194.171.12.150", "194.171.12.150");
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
