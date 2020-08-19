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

import com.google.netpcapanalysis.dao.PCAPParserDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPParserDao;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import java.io.IOException;

/** Servlet that puts raw PCAP files to Datastore by calling PCAPParserDao. */
@WebServlet("/PCAP-data")
public class PacketParserServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Retrieve user input to determine file name.
    String file = request.getParameter("file");
    // Retrieve user input to determine file description.
    String description = request.getParameter("description");
    PCAPParserDao parser = new PCAPParserDaoImpl(file, description);
    parser.parseRaw();
    parser.putDatastore(); 

    // Respond with the result.
    response.sendRedirect("/packet.html");
  }
} // end of PacketParserServlet class