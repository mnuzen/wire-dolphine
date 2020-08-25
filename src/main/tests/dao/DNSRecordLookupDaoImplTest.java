package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.netpcapanalysis.dao.ReverseDNSLookupDaoImpl;
import com.google.netpcapanalysis.dao.ReverseDNSLookupDaoImpl.GoogleDNS;
import com.google.netpcapanalysis.interfaces.dao.ReverseDNSLookupDao;
import com.google.netpcapanalysis.models.DNSRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DNSRecordLookupDaoImplTest {

  public static ReverseDNSLookupDaoImpl dns;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Before
  public void setup() {
    helper.setUp();
    dns = new ReverseDNSLookupDaoImpl();
  }

  @Test
  public void server() {
    String auth = "{\n"
        + "  \"Status\": 0,\n"
        + "  \"TC\": false,\n"
        + "  \"RD\": true,\n"
        + "  \"RA\": true,\n"
        + "  \"AD\": false,\n"
        + "  \"CD\": false,\n"
        + "  \"Question\": [\n"
        + "    {\n"
        + "      \"name\": \"8.8.8.8.in-addr.arpa.\",\n"
        + "      \"type\": 12\n"
        + "    }\n"
        + "  ],\n"
        + "  \"Answer\": [\n"
        + "    {\n"
        + "      \"name\": \"8.8.8.8.in-addr.arpa.\",\n"
        + "      \"type\": 12,\n"
        + "      \"TTL\": 20735,\n"
        + "      \"data\": \"dns.google.\"\n"
        + "    }\n"
        + "  ]\n"
        + "}";
    DNSRecord rec = dns.createRecordFromGoogleDNS(new Gson().fromJson(auth, GoogleDNS.class), "8.8.8.8");
    assertEquals("dns.google", rec.getDomain());
    assertTrue(rec.isServer());
    assertFalse(rec.isAuthority());
  }

  @Test
  public void cdn() {
    String auth = "{\n"
        + "  \"Status\": 3,\n"
        + "  \"TC\": false,\n"
        + "  \"RD\": true,\n"
        + "  \"RA\": true,\n"
        + "  \"AD\": true,\n"
        + "  \"CD\": false,\n"
        + "  \"Question\": [\n"
        + "    {\n"
        + "      \"name\": \"172.217.14.206.\",\n"
        + "      \"type\": 12\n"
        + "    }\n"
        + "  ],\n"
        + "  \"Authority\": [\n"
        + "    {\n"
        + "      \"name\": \".\",\n"
        + "      \"type\": 6,\n"
        + "      \"TTL\": 86354,\n"
        + "      \"data\": \"a.root-servers.net. nstld.verisign-grs.com. 2020082401 1800 900 604800 86400\"\n"
        + "    }\n"
        + "  ]\n"
        + "}";
    DNSRecord rec = dns.createRecordFromGoogleDNS(new Gson().fromJson(auth, GoogleDNS.class), "172.217.14.206");
    assertEquals("verisign-grs.com", rec.getDomain());
    assertFalse(rec.isServer());
    assertTrue(rec.isAuthority());
  }
}
