import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;
import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BenchmarkGeoDB {

  public static Random r = new Random();

  public static void main(String[] args) throws Exception{
    File database = new File("./data/GeoLite2-City.mmdb");

    DatabaseReader reader = new DatabaseReader.Builder(database).build();

    int reqs = (int) 1e6;
    int trials = 3;
    double result = averagedBenchmark(reader, reqs, trials);
    System.out.printf("averaged %.0f ms for %d requests, %.2f rps", result, reqs, reqs / result * 1000);
  }

  public static double averagedBenchmark(DatabaseReader reader, int requests, int trials) throws Exception {
    // throw out 1st trial to allow for jit
    benchmark(reader, requests);
    System.out.println("Finished JIT");
    List<Long> results = new ArrayList<>();
    for (int i = 0; i < trials; i++) {
      results.add(benchmark(reader, requests));
    }
    double avg = 0;
    for (long result: results) {
      avg += result;
    }
    return avg / trials;
  }

  public static long benchmark(DatabaseReader reader, int requests) throws Exception {
    InetAddress[] ips = new InetAddress[requests];
    for (int i = 0; i < requests; i++) {
      ips[i] = genIP();
    }
    long start = System.currentTimeMillis();
    System.out.println("Starting benchmark");
    for (int i = 0; i < requests; i++) {
      try {
        CityResponse response = reader.city(ips[i]);
      } catch (AddressNotFoundException e) { }
    }
    long end = System.currentTimeMillis() - start;
    System.out.format("finished %.0f requests in %d ms\n", 1e6, end);
    return end;
  }

  public static InetAddress genIP() throws Exception {
    InetAddress ip = null;
    while (ip == null || ip.isLoopbackAddress() ||
        ip.isAnyLocalAddress() ||
        ip.isLinkLocalAddress() ||
        ip.isSiteLocalAddress()) {
      ip = InetAddress.getByName(r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256));
    }
    return ip;
  }
}