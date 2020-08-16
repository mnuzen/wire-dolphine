package com.google.netpcapanalysis.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.dao.UtilityPCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.UtilityPCAPDao;
import com.google.netpcapanalysis.models.FileAttribute;
import java.util.ArrayList;



@WebServlet("/files")
public class FileServlet extends HttpServlet {
  private static final String fileEntity= "File_Attributes";
  private UtilityPCAPDao pcapUtility = new UtilityPCAPDaoImpl();
  private PCAPDao datastore = new PCAPDaoImpl();
  ArrayList<FileAttribute> fileList = new ArrayList<>();
  
  

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

      fileList = datastore.getFileAttributes(fileEntity);
  
      String json = convertToJsonUsingGson(fileList);
      response.setContentType("application/json;");
      response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      // Retrieve user input to determine file name.
      String file = request.getParameter("file");
     

      response.sendRedirect("/");
    }
  
  
    private String convertToJsonUsingGson(ArrayList<FileAttribute> data) {
      Gson gson = new Gson();
      String json = gson.toJson(data);
      return json;
    }

}