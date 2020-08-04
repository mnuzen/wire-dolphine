package com.google.netpcapanalysis.servlets;

import com.google.netpcapanalysis.lookup_ip.TestMaliciousIP;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;



@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        TestMaliciousIP IPtest = new TestMaliciousIP();

        IPtest.test();
        IPtest.results();
        
        response.sendRedirect("/index.html");
    }

}
