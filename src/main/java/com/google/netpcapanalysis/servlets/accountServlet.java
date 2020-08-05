
package com.google.sps.servlets;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/account_info")
public class accountServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    UserService userService = UserServiceFactory.getUserService();

   ArrayList<String> accountInfo = new ArrayList<String>();

    if (userService.isUserLoggedIn()) {
      String urlToRedirectToAfterUserLogsOut = "/";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
      String userEmail = userService.getCurrentUser().getEmail();

      accountInfo.add(logoutUrl);
      accountInfo.add(userEmail);
  
      } else {
        String urlToRedirectToAfterUserLogsIn = "/";
        String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
        accountInfo.add(loginUrl);
        accountInfo.add("NULL");
      }

      String json = convertToJsonUsingGson(accountInfo);
      response.setContentType("application/json;");
      response.getWriter().println(json);
  }

  private String convertToJsonUsingGson(ArrayList<String> data) {
    Gson gson = new Gson();
    String json = gson.toJson(data);
    return json;
  }
}

