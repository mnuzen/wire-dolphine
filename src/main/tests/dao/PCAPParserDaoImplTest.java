package dao;

import static org.junit.Assert.assertEquals;
import com.google.netpcapanalysis.dao.PCAPParserDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPParserDao;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;

import org.junit.Before;
import org.junit.Test;

import java.util.*; 
import java.io.*;


public class PCAPParserDaoImplTest {
  public PCAPParserDao parser;
  public String FILENAME = "files/file2.pcap";
  private String IP1 = "176.126.243.198";
  private String IP2 = "185.47.63.113";
  private String UDP = "UDP";
  private int FREQ = 2;

  @Before
  public void setup() throws IOException {
    parser = new PCAPParserDaoImpl(FILENAME);
    parser.parseRaw();
    parser.processData();
    //parser.putDatastore();
  }

  @Test
  public void testRawSource() {
    // extract raw sources
    ArrayList<String> rawSources = new ArrayList<String>();
    ArrayList<PCAPdata> allData = parser.getAllPCAP();
    for (PCAPdata packet : allData) {
      rawSources.add(packet.source);
    }

    // create sample sources
    ArrayList<String> comparison = new ArrayList<String>();
    comparison.add(IP1);
    comparison.add(IP2);

    // compare
    assertEquals(rawSources, comparison);
  }

  @Test
  public void testRawDestination() {
    // extract raw destinations
    ArrayList<String> rawDestinations = new ArrayList<String>();
    ArrayList<PCAPdata> allData = parser.getAllPCAP();
    for (PCAPdata packet : allData) {
      rawDestinations.add(packet.destination);
    }

    // create sample sources
    ArrayList<String> comparison = new ArrayList<String>();
    comparison.add(IP2);
    comparison.add(IP1);

    // compare
    assertEquals(rawDestinations, comparison);
  }

  @Test
  public void testProtocol() {
    // extract raw destinations
    ArrayList<String> protocols = new ArrayList<String>();
    ArrayList<PCAPdata> allData = parser.getAllPCAP();
    for (PCAPdata packet : allData) {
      protocols.add(packet.protocol);
    }

    // create sample sources
    ArrayList<String> comparison = new ArrayList<String>();
    comparison.add(UDP);
    comparison.add(UDP);

    // compare
    assertEquals(protocols, comparison);
  }

  /* Verify that the frequency of connections has been properly incremented after data has been processed. */
  @Test
  public void testFrequency() {
    HashMap<String, PCAPdata> finalPCAP = parser.getFinalPCAP();
    int frequency = finalPCAP.get(IP2).getFrequency();
    int comparison = FREQ;
    assertEquals(frequency, comparison);
  }

}
