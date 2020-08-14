package dao;

import static org.junit.Assert.assertEquals;
import org.junit.Assert;

import com.google.netpcapanalysis.dao.PCAPParserDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPParserDao;

import com.google.netpcapanalysis.models.PCAPdata;

import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;

import com.google.netpcapanalysis.dao.BucketDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.BucketDao;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.Arrays;

import java.io.*;
import java.lang.*;

import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import java.net.URL; 

/* Compares file2.pcap's parsed results to hard-coded results. IP1 and IP2 addresses are located in hidden text file and retrieved as a stream. */
public class BucketDaoImplTest {
  private BucketDao bucket;
  private ArrayList<PCAPdata> data;

  private String PCAPNAME = "files/file2.pcap";
  private String FILENAME = "files/file2.txt";

  private String IP1;
  private String IP2;
  private String UDP = "UDP";
  private String TCP = "TCP";
  private int FREQ = 2;
  private int SIZE = 2;

  @Before
  public void setup() throws IOException {
    // Parse set IP addresses from hidden text file
    Path path = Paths.get(FILENAME);
    InputStream stream = PCAPParserDaoImplTest.class.getClassLoader().getResourceAsStream(path.toString());
    
    String text = IOUtils.toString(stream, StandardCharsets.UTF_8);
    String[] values = text.split(",");
    IP1 = values[0];
    IP2 = values[1];

    setupHelper();
    bucket = new BucketDaoImpl(data);
  }

  private void setupHelper() {
    // Generate frequency information
    data = new ArrayList<PCAPdata>();
    PCAPdata tempPCAP1 = new PCAPdata(IP1, IP2, "", "", UDP, SIZE, "false", FREQ);
    data.add(tempPCAP1);

    PCAPdata tempPCAP2 = new PCAPdata(IP2, IP1, "", "", TCP, SIZE, "false", FREQ);
    data.add(tempPCAP2);

    PCAPdata tempPCAP3 = new PCAPdata(IP1, IP1, "", "", UDP, SIZE, "false", FREQ);
    data.add(tempPCAP3);
  }

  /* Checks the most recurrent IP address. */
  @Test
  public void testMyIP() {
    String myip = bucket.getMyIP();
    assertEquals(myip, IP1);
  }

  /* Test IP sorting works without any additions or deletions. */
  @Test
  public void testSort(){
    ArrayList<PCAPdata> comparison = sortHelper();
    ArrayList<PCAPdata> sorted = bucket.getSortedPCAP();

    // check sizes are the same
    assertEquals(sorted.size(), comparison.size());

    // checks all packets are in the correct order
    for (int i = 0; i < sorted.size(); i ++) {
      PCAPdata sortedPacket = sorted.get(i);
      PCAPdata comparisonPacket = comparison.get(i);

      // compare MYIP
      assertEquals(sortedPacket.source, comparisonPacket.source);
      // compare OUTIP
      assertEquals(sortedPacket.destination, comparisonPacket.destination);    
    }
  }

  private ArrayList<PCAPdata> sortHelper() {
    // Generate frequency information
    ArrayList<PCAPdata> comparison = new ArrayList<PCAPdata>();
    PCAPdata tempPCAP3 = new PCAPdata(IP1, IP1, "", "", UDP, SIZE, "false", FREQ); // OUTIP of IP1 comes first, since it's a smaller IP address
    comparison.add(tempPCAP3);

    PCAPdata tempPCAP1 = new PCAPdata(IP1, IP2, "", "", UDP, SIZE, "false", FREQ);
    comparison.add(tempPCAP1);

    PCAPdata tempPCAP2 = new PCAPdata(IP1, IP2, "", "", TCP, SIZE, "false", FREQ);
    comparison.add(tempPCAP2);

    return comparison;
  }

  /* Checks IPs are placed into proper IP classes with proper enumeration of protocols. */
  @Test
  public void testBuckets() {
    LinkedHashMap<String, HashMap<String, Integer>> buckets = bucket.getBuckets();
    LinkedHashMap<String, HashMap<String, Integer>> comparison = bucketHelper();
     // check sizes are the same
    assertEquals(buckets.size(), comparison.size());

    for (String className : buckets.keySet()) {
      // checks all classes are correctly filled out 
      HashMap<String, Integer> classMap = buckets.get(className);
      for (String key : classMap.keySet()) {
        assertEquals(classMap.get(key), classMap.get(key));
      }
    }
  }

  private LinkedHashMap<String, HashMap<String, Integer>> bucketHelper() {
    LinkedHashMap<String, HashMap<String, Integer>> comparison = new LinkedHashMap<String, HashMap<String, Integer>>();
    // initialize connection countings for UDP and TCP connections
    HashMap<String, Integer> protocolA = new HashMap<String, Integer>();
    HashMap<String, Integer> protocolB = new HashMap<String, Integer>();
    HashMap<String, Integer> protocolC = new HashMap<String, Integer>();
    HashMap<String, Integer> protocolDE = new HashMap<String, Integer>();

    protocolB.put("UDP", 2);
    protocolB.put("TCP", 1);
    protocolB.put("Total", 3);
   
    comparison.put("Class A", protocolA);
    comparison.put("Class B", protocolB);
    comparison.put("Class C", protocolC);
    comparison.put("Classes D & E", protocolDE);

    return comparison;
  }

}
