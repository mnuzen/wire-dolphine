package com.google.netpcapanalysis.mockdata;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;

import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.utils.UtilityPCAP;
import com.google.netpcapanalysis.models.FileAttribute;

public class MockDataLoader {
  // CSV format:
  // Source,Destination,Domain,Location,Protocal,Size,Flagged,Frequency

  public MockDataLoader() {

  }

  public void LoadData(ArrayList<PCAPdata> dataTable, String csvFile, String description) { // uploads to datastore
    PCAPDao dataBase = new PCAPDaoImpl();

    String entityName = UtilityPCAP.hashText(csvFile);

    dataBase.setPCAPObjects(dataTable, entityName);
    String myip = UtilityPCAP.findMyIP(dataTable);

    FileAttribute data = new FileAttribute(entityName, csvFile, myip, description);
    dataBase.setFileAttribute(data);
  }

  // Source,Destination,Protocal,Size
  public ArrayList<PCAPdata> CSVDataLoader(String csvFile) {
    ArrayList<PCAPdata> data = new ArrayList<PCAPdata>();
    String line = "";
    String cvsSplitBy = ",";

    try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

      while ((line = br.readLine()) != null) {

        // use comma as separator
        String[] pcapLine = line.split(cvsSplitBy);

        PCAPdata tempPCAP = new PCAPdata(pcapLine[0], pcapLine[1], pcapLine[2],
            Integer.parseInt(pcapLine[3]));

        data.add(tempPCAP);
      }

    } catch (IOException e) {
      System.out.println("CVS file failed to load");
      e.printStackTrace();
    }
    return data;
  }

  public void CSVDataUpload(String csvFile, String description) {
    LoadData(CSVDataLoader(csvFile), csvFile, description);
  }

}
