package com.google.netpcapanalysis.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.netpcapanalysis.dao.MaliciousIPDaoImpl;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.MaliciousIPDao;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.models.MaliciousPacket;
import com.google.netpcapanalysis.models.PCAPdata;




@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {


        MaliciousIPDao lookup = new MaliciousIPDaoImpl();
        PCAPdata temp = new PCAPdata("210.48.204.118","210.48.204.118","blank","blank","blank",4,false,4);
        lookup.isMalicious(temp);

        
        response.sendRedirect("/index.html");
    }

}