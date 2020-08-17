package com.google.netpcapanalysis.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import com.google.gson.Gson;

import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.dao.UtilityPCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.UtilityPCAPDao;
import com.google.netpcapanalysis.models.FileAttribute;
import java.util.ArrayList;



@WebServlet("/list-of-files")
public class FileServlet extends HttpServlet {
  private static final String fileEntity= "File_Attributes";
  private UtilityPCAPDao pcapUtility = new UtilityPCAPDaoImpl();
  
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      ArrayList<FileAttribute> fileList = new ArrayList<>();
      PCAPDao datastore = new PCAPDaoImpl();

      fileList = datastore.getFileAttributes(fileEntity);
  
      String json = pcapUtility.convertFileToJson(fileList);
      response.setContentType("application/json;");
      response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      
      String file = request.getParameter("file");

      pcapUtility.setSessionEntity(request, file);

      Cookie ck=new Cookie("uname", file);//creating cookie object  
      response.addCookie(ck);//adding cookie in the response  

      response.sendRedirect("/");
    }

}