package com.google.netpcapanalysis.dao;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.interfaces.dao.MaliciousIPDao;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.models.MaliciousPacket;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.google.netpcapanalysis.models.Flagged;
import java.util.ArrayList;

import com.google.netpcapanalysis.dao.KeystoreDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.KeystoreDao;

import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;

public class  MaliciousIPDaoImpl implements MaliciousIPDao{

    private KeystoreDao keystoreDao = new KeystoreDaoImpl();
    private final String AUTH0_API_KEY = keystoreDao.getKeystore().getAuth0APIKey(); //null pointer error if keystore fails to load

    private static final String FLAGGED_FALSE = "Resource not found";
    private static final String FLAGGED_TRUE = "200: OK";
    private static final String REQUEST_LIMIT = "Rate limit exceeded";

    private PCAPDao ipCache = new PCAPDaoImpl();

    public MaliciousIPDaoImpl(){
    }

    private PCAPdata isMalicious(PCAPdata data)
    {
        HttpResponse<String> result;

        String searchDB = ipCache.searchMaliciousDB(data.destination);
        if(searchDB.equalsIgnoreCase(Flagged.TRUE))
        {
            data.flagged = Flagged.TRUE;
        }
        else if(searchDB.equalsIgnoreCase(Flagged.FALSE)){
            data.flagged = Flagged.FALSE;
        }
        else{
            System.out.println("Else statement");
            try {
                result = Unirest.get("https://signals.api.auth0.com/badip/" + data.destination)
                        .header("X-Auth-Token", AUTH0_API_KEY)
                        .asString();
           
                if(result.getBody().equalsIgnoreCase(FLAGGED_FALSE))
                {
                    data.flagged = Flagged.FALSE;
                    MaliciousPacket tempPacket = new MaliciousPacket(data.destination,data.flagged);
                    ipCache.setMaliciousIPObjects(tempPacket);
                }
                else if(result.getBody().equalsIgnoreCase(FLAGGED_TRUE))
                {
                    data.flagged = Flagged.TRUE; 
                    MaliciousPacket tempPacket = new MaliciousPacket(data.destination,data.flagged);
                    ipCache.setMaliciousIPObjects(tempPacket);
                }
                else if (result.getBody().contains(REQUEST_LIMIT))
                {
                    data.flagged = Flagged.UNKNOWN; 
                }
                else{
                    data.flagged = Flagged.ERROR; 
                }
    
            } catch (UnirestException e) {
                System.out.println("IP lookup failed");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return data;
    }
    
    private String isMalicious(String ip)
    {
        HttpResponse<String> apiResult;
        String result;

        String searchDB = ipCache.searchMaliciousDB(ip);
        if(searchDB.equalsIgnoreCase(Flagged.TRUE))
        {
            result = Flagged.TRUE;
        }
        else if(searchDB.equalsIgnoreCase(Flagged.FALSE)){
            result = Flagged.FALSE;
        }
        else{
            System.out.println("Else statement");
            try {
                apiResult = Unirest.get("https://signals.api.auth0.com/badip/" + ip)
                        .header("X-Auth-Token", AUTH0_API_KEY)
                        .asString();
           
                if(apiResult.getBody().equalsIgnoreCase(FLAGGED_FALSE))
                {
                    result = Flagged.FALSE;
                    MaliciousPacket tempPacket = new MaliciousPacket(ip, result);
                    ipCache.setMaliciousIPObjects(tempPacket);
                }
                else if(apiResult.getBody().equalsIgnoreCase(FLAGGED_TRUE))
                {
                    result = Flagged.TRUE; 
                    MaliciousPacket tempPacket = new MaliciousPacket(ip, result);
                    ipCache.setMaliciousIPObjects(tempPacket);
                }
                else if (apiResult.getBody().contains(REQUEST_LIMIT))
                {
                    result = Flagged.UNKNOWN; 
                }
                else{
                    result = Flagged.ERROR; 
                }
    
            } catch (UnirestException e) {
                System.out.println("IP lookup failed");
                result = Flagged.ERROR;
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }
    
    public void run(String searchEntity){
        PCAPDao datastore = new PCAPDaoImpl();
        ArrayList<PCAPdata> allData = datastore.getPCAPObjects(searchEntity); 
        ArrayList<PCAPdata> uniqueData = datastore.getUniqueIPs(allData); 
        String flaggeValue;

        for(PCAPdata packet : uniqueData){
            flaggeValue = isMalicious(packet.destination);
            datastore.updateFlagged(searchEntity, packet, flaggeValue);
        }
    }
}