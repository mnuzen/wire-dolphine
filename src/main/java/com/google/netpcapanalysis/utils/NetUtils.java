package com.google.netpcapanalysis.utils;

import java.util.ArrayList;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.netpcapanalysis.models.FileAttribute;
import com.google.netpcapanalysis.models.PCAPdata;

public class NetUtils {
  /**
   * @return the request parameter, or the default value if the parameter was not specified by the
   * client
   */
  public static String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  static public String convertPCAPdataToJson(List<PCAPdata> data) {
    Gson gson = new Gson();
    String json = gson.toJson(data);
    return json;
  }

  static public String convertFileToJson(List<FileAttribute> data) {
    Gson gson = new Gson();
    String json = gson.toJson(data);
    return json;
  }
}
