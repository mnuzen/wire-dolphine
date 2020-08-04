
package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class loginForm extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
      response.getWriter().println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">");
      response.getWriter().println("<div class=\"center\">");
      response.getWriter().println("<h1>Welcome " + userEmail + "</h1>");
      response.getWriter().println("<h3>Logout <a href=\"" + logoutUrl + "\">here</a>.</h3>");
      response.getWriter().println("</div>");
    } else {
      String urlToRedirectToAfterUserLogsIn = "/";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      response.getWriter().println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">");
      response.getWriter().println("<div class=\"center\">");
      response.getWriter().println("<h2>You must login inorder to submit a comment</h2>");
      response.getWriter().println("<h3>Login <a href=\"" + loginUrl + "\">here</a>.</h3>");
      response.getWriter().println("</div>");
    }
  }
}

