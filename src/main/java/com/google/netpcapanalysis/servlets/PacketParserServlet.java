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

import com.google.netpcapanalysis.dao.PCAPParserDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPParserDao;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*; 

import java.io.IOException;

/** Servlet that puts raw PCAP files to Datastore by calling PCAPParserDao. */
@WebServlet("/PCAP-data")
public class PacketParserServlet extends HttpServlet {
  HashMap<String, PCAPdata> allPCAP = new HashMap<String, PCAPdata>();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Retrieve user input to determine file name.
    String file = request.getParameter("file-input");
    String description = request.getParameter("description");

    PCAPParserDao parser = new PCAPParserDaoImpl(file, description);
    parser.parseRaw();
    parser.putDatastore(); 

    // Respond with the result.
    response.sendRedirect("/packet.html");
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

} // end of PacketParserServlet class