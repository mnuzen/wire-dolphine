package com.google.netpcapanalysis.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.models.FileAttribute;
import java.util.ArrayList;
import com.google.netpcapanalysis.utils.SessionManager;
import com.google.netpcapanalysis.utils.NetUtils;



@WebServlet("/list-of-files")
public class FileServlet extends HttpServlet {
  private static final String fileEntity= "File_Attributes";
  
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      ArrayList<FileAttribute> fileList = new ArrayList<>();
      PCAPDao datastore = new PCAPDaoImpl();

      fileList = datastore.getFileAttributes(fileEntity);
  
      String json = NetUtils.convertFileToJson(fileList);
      response.setContentType("application/json;");
      response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      
      String file = request.getParameter("file");

      SessionManager.setSessionEntity(request, file);

      response.sendRedirect("/");
    }

}