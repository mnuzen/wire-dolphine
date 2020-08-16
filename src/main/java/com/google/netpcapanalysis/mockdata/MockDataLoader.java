package com.google.netpcapanalysis.mockdata;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;

import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.dao.UtilityPCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.UtilityPCAPDao;
import com.google.netpcapanalysis.models.FileAttribute;

public class MockDataLoader {
  // CSV format:
  // Source,Destination,Domain,Location,Protocal,Size,Flagged,Frequency

  public MockDataLoader() {

  }

  public void LoadData(ArrayList<PCAPdata> dataTable, String csvFile) { // uploads to datastore
    PCAPDao dataBase = new PCAPDaoImpl();
    UtilityPCAPDao pcapUtility = new UtilityPCAPDaoImpl();

    String entityName = pcapUtility.hashText(csvFile);

    dataBase.setPCAPObjects(dataTable, entityName);
    String myip = pcapUtility.findMyIP(dataTable);

    FileAttribute data = new FileAttribute(entityName, csvFile, myip);
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

  public void CSVDataUpload(String csvFile) {
    LoadData(CSVDataLoader(csvFile), csvFile);
  }

}
