package com.google.netpcapanalysis.dao;

import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.interfaces.dao.KeystoreDao;
import com.google.netpcapanalysis.interfaces.dao.MaliciousIPDao;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.models.MaliciousPacket;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.google.netpcapanalysis.models.Flagged;
import java.util.ArrayList;

public class  MaliciousIPDaoImpl implements MaliciousIPDao{

    private KeystoreDao keystoreDao = new KeystoreDaoImpl();
    private final String AUTH0_API_KEY = keystoreDao.getKeystore().getAuth0APIKey(); //null pointer error if keystore fails to load

    private static final String FLAGGED_FALSE = "Resource not found";
    private static final String FLAGGED_TRUE = "200: OK";
    private static final String REQUEST_LIMIT = "Rate limit exceeded";

    private PCAPDao ipCache = new PCAPDaoImpl();
    private String myIP;

    public MaliciousIPDaoImpl(){
    }

    public PCAPdata isMalicious(PCAPdata data)
    {
        HttpResponse<String> result;
        String searchIP;

        //set searchIP to not myIP
        if(data.source.equals(myIP))
        {
            searchIP = data.destination;
        }
        else{
            searchIP = data.source;
        }

        //checks to see if cache has seen IP already
        String searchDB = ipCache.searchMaliciousDB(searchIP);
        if(searchDB.equalsIgnoreCase(Flagged.TRUE))
        {
            data.flagged = Flagged.TRUE;
        }
        else if(searchDB.equalsIgnoreCase(Flagged.FALSE)){
            data.flagged = Flagged.FALSE;
        }
        else{
            try {
                result = Unirest.get("https://signals.api.auth0.com/badip/" + searchIP)
                        .header("X-Auth-Token", AUTH0_API_KEY)
                        .asString();
           
                if(result.getBody().equalsIgnoreCase(FLAGGED_FALSE))
                {
                    data.flagged = Flagged.FALSE;
                    MaliciousPacket tempPacket = new MaliciousPacket(searchIP,data.flagged);
                    ipCache.setMaliciousIPObjects(tempPacket);
                }
                else if(result.getBody().equalsIgnoreCase(FLAGGED_TRUE))
                {
                    data.flagged = Flagged.TRUE; 
                    MaliciousPacket tempPacket = new MaliciousPacket(searchIP,data.flagged);
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
                data.flagged = Flagged.ERROR; 
                e.printStackTrace();
            }
        }
        return data;
    }
    
    public ArrayList<PCAPdata> run(ArrayList<PCAPdata> allData, String myIP){
        this.myIP = myIP;

        for(PCAPdata packet : allData){
            packet = isMalicious(packet);
        }

        return allData;
    }
}