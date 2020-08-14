package com.google.netpcapanalysis.servlets;

import com.google.netpcapanalysis.caching.CacheBuilder;
import com.google.netpcapanalysis.caching.CacheBuilder.CacheType;
import com.google.netpcapanalysis.interfaces.caching.Cache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;

@WebServlet("/bench")
public class BenchmarkServlet extends HttpServlet {

  public static Random r = new Random();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      URL geoDBUrl = BenchmarkServlet.class.getClassLoader().getResource("GeoLite2-City.mmdb");
      File database = new File(geoDBUrl.toURI());
      DatabaseReader reader = new DatabaseReader.Builder(database).build();

      int reqs = (int) 1e6;
      int trials = 3;
      double result = averagedBenchmark(reader, reqs, trials);
      response.getWriter()
          .printf("averaged %.0f ms for %d requests, %.2f rps\n", result, reqs, reqs / result * 1000);
      double result3 = averagedCachedBenchmark(reader, reqs, trials);
      response.getWriter()
          .printf("averaged cached %.0f ms for %d requests, %.2f rps\n", result3, reqs,
              reqs / result3 * 1000);
    } catch (Exception e) {
      e.printStackTrace();
    }
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
    InetAddress[] ips = genIPsUneven(requests);

    long start = System.currentTimeMillis();
    System.out.println("Starting benchmark");
    for (int i = 0; i < requests; i++) {
      try {
        CityResponse response = reader.city(ips[i]);
      } catch (AddressNotFoundException e) {
      }
    }
    long end = System.currentTimeMillis() - start;
    System.out.format("finished %.0f requests in %d ms\n", 1e6, end);
    return end;
  }

  public static long benchmarkCached(DatabaseReader reader, int requests) throws Exception {
    Cache<InetAddress, String> cache = new CacheBuilder<InetAddress, String>()
        .setCacheName("geolocation")
        .setExpiration(600000) // 10 min
        .setMaxItems(1000)
        .setType(CacheType.DATASTORE)
        .build();

    InetAddress[] ips = genIPsUneven(requests);

    long start = System.currentTimeMillis();
    System.out.println("Starting benchmark");
    for (int i = 0; i < requests; i++) {
      try {
        if (cache.get(ips[i]) == null) {
          CityResponse response = reader.city(ips[i]);
          if (ips[i] != null && response != null && response.getCountry().getName() != null) {
            cache.put(ips[i], response.getCountry().getName());
          }
        }
      } catch (AddressNotFoundException e) {
      }
    }
    long end = System.currentTimeMillis() - start;
    System.out.format("finished %.0f requests in %d ms\n", 1e6, end);
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
