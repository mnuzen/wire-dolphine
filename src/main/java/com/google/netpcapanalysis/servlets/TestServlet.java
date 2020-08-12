package com.google.netpcapanalysis.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.netpcapanalysis.dao.MaliciousIPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.MaliciousIPDao;
import com.google.netpcapanalysis.models.PCAPdata;

import com.google.netpcapanalysis.models.Flagged;




@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
      /*
        MaliciousIPDao lookup = new MaliciousIPDaoImpl();
        PCAPdata bad = new PCAPdata("210.48.204.118","210.48.204.118","blank","blank","blank",4,Flagged.UNKNOWN,4);
        PCAPdata good = new PCAPdata("8.8.8.8","8.8.8.8","blank","blank","blank",4,Flagged.UNKNOWN,4);
        bad = lookup.isMalicious(bad);
        good = lookup.isMalicious(good);
        */

        
        response.sendRedirect("/index.html");
    }

}