/*
Benchmarks Cache lookup

Without Cache 210 enities
73ms per lookup
15,408 ms totak
with
2ms per lookuo
562 ms each

95% decrease in lookup time
*/

package com.google.netpcapanalysis.servlets;

import java.io.IOException;
import java.util.List;

import com.google.netpcapanalysis.models.FileAttribute;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.interfaces.dao.ReverseDNSLookupDao;
import com.google.netpcapanalysis.dao.MaliciousIPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.MaliciousIPDao;
import com.google.netpcapanalysis.dao.GeolocationDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.GeolocationDao;
import com.google.netpcapanalysis.dao.ReverseDNSLookupDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.ReverseDNSLookupDao;

import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.netpcapanalysis.utils.SessionManager;
import com.google.netpcapanalysis.utils.UtilityPCAP;
import com.google.netpcapanalysis.utils.NetUtils;



@WebServlet("/test")
public class TestServlet extends HttpServlet {

    private PCAPDao datastore = new PCAPDaoImpl();
    private MaliciousIPDao maliciousLookup = new MaliciousIPDaoImpl();
    private List<PCAPdata> dataTable;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String entityName = SessionManager.getSessionEntity(request);
        FileAttribute entity = datastore.getFileAttribute(entityName);

        long avgTime;
        long totalTime;
        long start;
        long end;

        avgTime = 0L;
        totalTime = 0L;
        start = System.currentTimeMillis();

        dataTable = datastore.getPCAPObjects(entityName);
        dataTable = UtilityPCAP.getUniqueIPs(dataTable);
        dataTable = maliciousLookup.run(dataTable, entity.myIP);

        end = System.currentTimeMillis();
        totalTime+=(end-start);
        avgTime = totalTime / dataTable.size();

        System.out.println("Entities Size " + dataTable.size() + "\n");
        System.out.println("Average lookup time: " + avgTime + "\n");
        System.out.println("Total lookup time: " + totalTime + "\n\n");

        //// second time Data will be in memory cache
        
        avgTime = 0L;
        totalTime = 0L;
        start = System.currentTimeMillis();

        dataTable = datastore.getPCAPObjects(entityName);
        dataTable = UtilityPCAP.getUniqueIPs(dataTable);
        dataTable = maliciousLookup.run(dataTable, entity.myIP);
        
        end = System.currentTimeMillis();
        totalTime+=(end-start);
        avgTime = totalTime / dataTable.size();

        System.out.println("Entities Size " + dataTable.size() + "\n");
        System.out.println("Average lookup time: " + avgTime + "\n");
        System.out.println("Total lookup time: " + totalTime + "\n");
        
        response.sendRedirect("/index.html");
    }
}