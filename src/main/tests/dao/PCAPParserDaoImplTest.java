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
import java.lang.*;

import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;

/* Compares file2.pcap's parsed results to hard-coded results. IP1 and IP2 addresses are located in hidden text file and retrieved as a stream. */
public class PCAPParserDaoImplTest {
  public PCAPParserDao parser;
  public String PCAPNAME = "resources/files/file2.pcap";
  private String FILENAME = "resources/files/file2.txt";

  private String IP1 = "176.126.243.198";
  private String IP2 = "185.47.63.113";
  private String UDP = "UDP";
  private int FREQ = 2;

  @Before
  public void setup() throws IOException {
    // Retrieve PCAPParser information
    parser = new PCAPParserDaoImpl(PCAPNAME);
    parser.parseRaw();
    parser.processData();

    // Parse set IP addresses from hidden text file
    /*InputStream stream = PCAPParserDaoImplTest.class.getClassLoader().getResourceAsStream(FILENAME);
    String text = IOUtils.toString(stream, StandardCharsets.UTF_8);
    String[] values = text.split(",");
    IP1 = values[0];
    IP2 = values[1];*/

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
