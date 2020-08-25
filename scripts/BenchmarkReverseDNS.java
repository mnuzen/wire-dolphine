import com.google.netpcapanalysis.caching.CacheBuilder;
import com.google.netpcapanalysis.caching.CacheBuilder.CacheType;
import com.google.netpcapanalysis.caching.CacheBuilder.Policy;
import com.google.netpcapanalysis.dao.ReverseDNSLookupDaoImpl;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import com.google.netpcapanalysis.interfaces.dao.ReverseDNSLookupDao;
import com.google.netpcapanalysis.models.DNSRecord;
import com.maxmind.geoip2.DatabaseReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BenchmarkReverseDNS {

  public static Random r = new Random();
  public static ReverseDNSLookupDaoImpl dao;
  public static int reqs = (int) 1e3;

  public static void main(String[] args) throws Exception {
    List<String> l = new ArrayList<>();

    File ips = new File("./resources/ips.txt");
    BufferedReader br = new BufferedReader(new FileReader(ips));

    String line;
    while ((line = br.readLine()) != null) {
      String[] split = line.split("\\s");
      String ip = split[0];
      int num = Integer.parseInt(split[1]);
      for (int i = 0; i < num; i++) {
        l.add(ip);
      }
    }

    Collections.shuffle(l);

    System.out.println(l.size());

    int trials = 3;
    double result = averagedBenchmark(reqs, trials);
    System.out
        .printf("averaged %.0f ms for %d requests, %.2f rps", result, reqs, reqs / result * 1000);
  }

  public static double averagedBenchmark(int requests, int trials)
      throws Exception {
    // throw out 1st trial to allow for jit
    benchmark(requests);
    System.out.println("Finished JIT");
    List<Long> results = new ArrayList<>();
    for (int i = 0; i < trials; i++) {
      results.add(benchmark(requests));
    }
    double avg = 0;
    for (long result : results) {
      avg += result;
    }
    return avg / trials;
  }

  public static long benchmark(int requests) throws Exception {
    dao = new ReverseDNSLookupDaoImpl();
    InetAddress[] ips = genIPsRandom(requests);

    long start = System.currentTimeMillis();
    System.out.println("Starting benchmark");
    for (int i = 0; i < requests; i++) {
      DNSRecord record = dao.lookup("8.8.8.8");
    }
    long end = System.currentTimeMillis() - start;
    System.out.format("finished %d requests in %d ms\n", reqs, end);
    System.out.println("hits: " + dao.cache.hits());
    System.out.println("misses: " + dao.cache.misses());
    return end;
  }
  public static InetAddress genIP() throws Exception {
    InetAddress ip = null;
    while (ip == null ||
        ip.isLoopbackAddress() ||
        ip.isAnyLocalAddress() ||
        ip.isLinkLocalAddress() ||
        ip.isSiteLocalAddress()) {
      ip = InetAddress.getByName(
          r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256));
    }
    return ip;
  }

  public static InetAddress[] genIPsUneven(int num) throws Exception {
    int ratio = 1000;
    InetAddress[] ips = new InetAddress[num];
    for (int i = 0; i < num / ratio; i++) {
      ips[i] = genIP();
    }
    for (int i = num / ratio; i < num; i++) {
      ips[i] = ips[(int) (Math.random() * i / 100)];
    }

    List<InetAddress> l = Arrays.asList(ips);
    Collections.shuffle(l);
    l.toArray(ips);
    return ips;
  }


  public static InetAddress[] genIPsRandom(int num) throws Exception {
    InetAddress[] ips = new InetAddress[num];
    for (int i = 0; i < num; i++) {
      ips[i] = genIP();
    }
    return ips;
  }
}