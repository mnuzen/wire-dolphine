// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License. 

package com.google.netpcapanalysis.servlets;

import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.dao.BucketDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.BucketDao;
import com.google.netpcapanalysis.utils.SessionManager;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.io.InputStream;
import java.io.IOException;

/** Servlet that retrieves and returns frequencies. */
@WebServlet("/PCAP-bucket")
public class BucketLoaderServlet extends HttpServlet {
  private String filename;
  private PCAPDao data = new PCAPDaoImpl();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String entityName = SessionManager.getSessionEntity(request);
    BucketDao bucket = new BucketDaoImpl(data.getPCAPObjects(entityName));
    LinkedHashMap<String, HashMap<String, Integer>> bucketData = bucket.getFinalBuckets();
    String json = convertToJsonUsingGson(bucketData);
    response.setContentType("application/json;");
    response.getWriter().println(json); 
  }

  private String convertToJsonUsingGson(LinkedHashMap<String, HashMap<String, Integer>> data) {
    Gson gson = new Gson();
    String json = gson.toJson(data);
    return json;
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

} // end of BucketLoaderServlet class