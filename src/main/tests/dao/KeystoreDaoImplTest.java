package dao;

import static org.junit.Assert.assertNotEquals;

import com.google.netpcapanalysis.dao.KeystoreDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.KeystoreDao;
import org.junit.Before;
import org.junit.Test;

public class KeystoreDaoImplTest {

  public static KeystoreDao store;

  @Before
  public void setup() {
    store = new KeystoreDaoImpl();
  }

  @Test
  public void keystoreExists() {
    assertNotEquals(null, store.getKeystore());
  }

  @Test
  public void keystoreContainsGeoKey()  {
    assertNotEquals("", store.getKeystore());
  }
}
