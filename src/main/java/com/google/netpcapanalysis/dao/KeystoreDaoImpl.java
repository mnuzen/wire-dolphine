package com.google.netpcapanalysis.dao;

import com.google.gson.Gson;
import com.google.netpcapanalysis.interfaces.dao.KeystoreDao;
import com.google.netpcapanalysis.models.Keystore;
import java.io.FileReader;

public class KeystoreDaoImpl implements KeystoreDao {

  public static final String KEYSTORE_LOCATION = "keystore.json";
  public Keystore keystore;

  public KeystoreDaoImpl() {
    try {
      String location = GeolocationDaoImpl.class.getClassLoader().getResource(KEYSTORE_LOCATION)
          .getFile();
      FileReader keystoreFile = new FileReader(location);
      Gson gson = new Gson();

      keystore = gson.fromJson(keystoreFile, Keystore.class);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  @Override
  public Keystore getKeystore() {
    return keystore;
  }
}
