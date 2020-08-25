import com.google.netpcapanalysis.dao.ReverseDNSLookupDaoImpl;
import com.google.netpcapanalysis.models.DNSRecord;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BenchmarkReverseDNS2 {

  public static Random r = new Random();
  public static ReverseDNSLookupDaoImpl dao;
  public static int reqs = (int) 1e3;
  public static List<String> l = new ArrayList<>();

  public static void main(String[] args) throws Exception {
    File ips = new File("./resources/ips.txt");
    BufferedReader br = new BufferedReader(new FileReader(ips));

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
    reqs = uniq;

    int trials = 3;
    double result = averagedBenchmark(trials);
    System.out
        .printf("averaged %.0f ms for %d requests, %.2f rps", result, reqs, reqs / result * 1000);
  }

  public static double averagedBenchmark(int trials)
      throws Exception {
    // throw out 1st trial to allow for jit
    benchmark();
    System.out.println("Finished JIT");
    List<Long> results = new ArrayList<>();
    for (int i = 0; i < trials; i++) {
      results.add(benchmark());
    }
    double avg = 0;
    for (long result : results) {
      avg += result;
    }
    return avg / trials;
  }

  public static long benchmark() {
    dao = new ReverseDNSLookupDaoImpl(true);
    long start = System.currentTimeMillis();
    System.out.println("Starting benchmark");

    List<DNSRecord> results = dao.lookup(l);
    long end = System.currentTimeMillis() - start;
    System.out.format("finished %d requests in %d ms\n", reqs, end);
    System.out.println("hits: " + dao.cache.hits());
    System.out.println("misses: " + dao.cache.misses());
    return end;
  }
}