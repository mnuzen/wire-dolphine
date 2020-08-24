import com.google.netpcapanalysis.caching.CacheBuilder;
import com.google.netpcapanalysis.caching.CacheBuilder.CacheType;
import com.google.netpcapanalysis.caching.CacheBuilder.Policy;
import com.google.netpcapanalysis.dao.ReverseDNSLookupDaoImpl;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import com.google.netpcapanalysis.interfaces.dao.ReverseDNSLookupDao;
import com.google.netpcapanalysis.models.DNSRecord;
import com.maxmind.geoip2.DatabaseReader;
import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BenchmarkReverseDNS {

  public static Random r = new Random();
  public static ReverseDNSLookupDao dao = new ReverseDNSLookupDaoImpl();
  public static int reqs = (int) 1e3;

  public static void main(String[] args) throws Exception {
    File database = new File("./resources/GeoLite2-City.mmdb");

    DatabaseReader reader = new DatabaseReader.Builder(database).build();

    int trials = 3;
    double result = averagedBenchmark(reader, reqs, trials);
    System.out
        .printf("averaged %.0f ms for %d requests, %.2f rps", result, reqs, reqs / result * 1000);
    double result3 = averagedCachedBenchmark(reader, reqs, trials);
    System.out
        .printf("averaged cached %.0f ms for %d requests, %.2f rps", result3, reqs,
            reqs / result3 * 1000);
  }

  public static double averagedCachedBenchmark(DatabaseReader reader, int requests, int trials)
      throws Exception {
    // throw out 1st trial to allow for jit
    benchmarkCached(reader, requests);
    System.out.println("Finished JIT");
    List<Long> results = new ArrayList<>();
    for (int i = 0; i < trials; i++) {
      results.add(benchmarkCached(reader, requests));
    }
    double avg = 0;
    for (long result : results) {
      avg += result;
    }
    return avg / trials;
  }

  public static double averagedBenchmark(DatabaseReader reader, int requests, int trials)
      throws Exception {
    // throw out 1st trial to allow for jit
    benchmark(reader, requests);
    System.out.println("Finished JIT");
    List<Long> results = new ArrayList<>();
    for (int i = 0; i < trials; i++) {
      results.add(benchmark(reader, requests));
    }
    double avg = 0;
    for (long result : results) {
      avg += result;
    }
    return avg / trials;
  }

  public static long benchmark(DatabaseReader reader, int requests) throws Exception {
    InetAddress[] ips = genIPsRandom(requests);

    long start = System.currentTimeMillis();
    System.out.println("Starting benchmark");
    for (int i = 0; i < requests; i++) {
      System.out.println(i);
      DNSRecord record = dao.lookup(ips[i].getHostAddress());
    }
    long end = System.currentTimeMillis() - start;
    System.out.format("finished %d requests in %d ms\n", reqs, end);
    return end;
  }

  public static long benchmarkCached(DatabaseReader reader, int requests) throws Exception {
    Cache<InetAddress, DNSRecord> cache = new CacheBuilder<InetAddress, DNSRecord>()
        .setCacheName("geolocation")
        .setPolicy(Policy.MAXIMUM_SIZE) // 10 min
        .setPolicyArgument(1000)
        .setCacheType(CacheType.MEMORY)
        .enableStatistics(true)
        .build();

    InetAddress[] ips = genIPsRandom(requests);

    long start = System.currentTimeMillis();
    System.out.println("Starting benchmark");
    for (int i = 0; i < requests; i++) {
      System.out.println(i);
      if (cache.get(ips[i]) == null) {
        DNSRecord record = dao.lookup(ips[i].getHostAddress());
        if (ips[i] != null && record != null) {
          cache.put(ips[i], record);
        }
      }
    }
    long end = System.currentTimeMillis() - start;
    System.out.format("finished %d requests in %d ms\n", reqs, end);
    System.out.println("hits: " + cache.hits());
    System.out.println("misses:" + cache.misses());
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