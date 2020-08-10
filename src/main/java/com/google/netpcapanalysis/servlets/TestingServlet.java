package com.google.netpcapanalysis.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.models.Flagged;

import com.google.netpcapanalysis.intergration_tests.MaliciousTest;




@WebServlet("/test")
public class TestingServlet extends HttpServlet {

  private static final int testsTotal = 2;
  private int testsPassed = 0;


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      
      //Test Mock dataloader

     //MaliciousTest
      MaliciousTest MaliciousTest = new MaliciousTest();
      PCAPdata bad = new PCAPdata("blank","210.48.204.118","blank","blank","blank",4,Flagged.UNKNOWN,4);
      testsPassed += MaliciousTest.run(bad, "true");


      /////////////
      System.out.format("Total tests: %d  |  Passed: %d  |  Failed: %d\n",
       testsTotal, testsPassed,(testsTotal-testsPassed));
      response.sendRedirect("/");
    }

}