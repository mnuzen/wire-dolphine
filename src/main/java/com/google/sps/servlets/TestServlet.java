package com.google.sps.servlets;

import com.google.sps.data.PCAPdata;
import com.google.sps.lookup_ip.MaliciousIPDao;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

//known IPS
// good 67.59.110.103
// bad 176.31.182.86

@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        PCAPdata packet = new PCAPdata("176.31.182.86","176.31.182.86","wikipedia.com",
        "United States","TCP",5,false,10);
        MaliciousIPDao ipTest = new MaliciousIPDao();
        packet = ipTest.isMalicious(packet); 



        response.setContentType("text/html;");
        response.getWriter().println("Is " + packet.source + " Malicious?   (" + packet.flagged + ")");
    }

}
