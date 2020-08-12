package dao;

import static org.junit.Assert.assertEquals;
import com.google.netpcapanalysis.dao.PCAPParserDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPParserDao;

import com.google.netpcapanalysis.models.PCAPdata;

import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;

import com.google.netpcapanalysis.dao.FrequencyDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.FrequencyDao;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import java.util.*; 
import java.io.*;
import java.lang.*;

import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import java.net.URL; 

/* Compares file2.pcap's parsed results to hard-coded results. IP1 and IP2 addresses are located in hidden text file and retrieved as a stream. */
public class FrequencyDaoImplTest {
  private FrequencyDao freq;
  private ArrayList<PCAPdata> data;

  private String PCAPNAME = "files/file2.pcap";
  private String FILENAME = "files/file2.txt";

  private String IP1;
  private String IP2;
  private String UDP = "UDP";
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

    // Generate frequency information
    data = new ArrayList<PCAPdata>();
    PCAPdata tempPCAP1 = new PCAPdata(IP1, IP2, "", "", UDP, SIZE, "false", FREQ);
    data.add(tempPCAP1);

    PCAPdata tempPCAP2 = new PCAPdata(IP2, IP1, "", "", UDP, SIZE, "false", FREQ);
    data.add(tempPCAP2);

    PCAPdata tempPCAP3 = new PCAPdata(IP1, IP1, "", "", UDP, SIZE, "false", FREQ);
    data.add(tempPCAP3);

    freq = new FrequencyDaoImpl(data);
    freq.loadFrequency();
  }

  /* Checks all packets have been put properly. */
  @Test
  public void testAllPCAP() {
    ArrayList<PCAPdata> allPCAP = freq.getAllPCAP();
    assertEquals(allPCAP, data);
  }

  /* Checks the most recurrent IP address. */
  @Test
  public void testMyIP() {
    String myip = freq.getMyIP();
    assertEquals(myip, IP1);
  }

  /* Checks number of unique packets. */
  @Test
  public void testDuplicates() {
    HashMap<String, PCAPdata> finalMap = freq.getFinalMap();
    int size = finalMap.size();
    int comparison = SIZE;
    assertEquals(size, comparison); // there should only be 2 unique connections: IP1/IP2 and IP1/IP1
  }

  /* Checks number of final nodes. */
  @Test
  public void testFinalNodes() {
    ArrayList<PCAPdata> finalFreq = freq.getFinalFreq();
    int size = finalFreq.size();
    int comparison = SIZE;
    assertEquals(size, comparison); // there should only be 2 nodes: IP1 and IP2
  }

}
