package com.google.netpcapanalysis.utils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionManager {
 
  public static String getSessionEntity(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    try{  
      String n = (String) session.getAttribute("entityName");  
      return n;
       }
      catch(Exception e)
       {
          return null;
        }
  }

  public static void setSessionEntity(HttpServletRequest request, String text) {
    HttpSession session = request.getSession();  
    session.setAttribute("entityName",text);  
  }
}
