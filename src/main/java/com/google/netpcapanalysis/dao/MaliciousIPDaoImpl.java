package com.google.netpcapanalysis.dao;

import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.interfaces.dao.KeystoreDao;
import com.google.netpcapanalysis.interfaces.dao.MaliciousIPDao;
import com.google.netpcapanalysis.models.PCAPdata;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.google.netpcapanalysis.models.Flagged;
import java.util.ArrayList;

import com.google.netpcapanalysis.caching.CacheBuilder;
import com.google.netpcapanalysis.caching.CacheBuilder.CacheType;
import com.google.netpcapanalysis.caching.CacheBuilder.Policy;
import com.google.netpcapanalysis.interfaces.caching.Cache;

public class  MaliciousIPDaoImpl implements MaliciousIPDao{

    private KeystoreDao keystoreDao = new KeystoreDaoImpl();
    private final String AUTH0_API_KEY = keystoreDao.getKeystore().getAuth0APIKey(); //null pointer error if keystore fails to load

    private static final String FLAGGED_FALSE = "Resource not found";
    private static final String FLAGGED_TRUE = "200: OK";
    private static final String REQUEST_LIMIT = "Rate limit exceeded";

    private Cache<String, String> ipCache;
    private String myIP;

    public MaliciousIPDaoImpl(){
        ipCache = new CacheBuilder<String, String>()
        .setCacheName("Malicious_IP_Cache")
        .setPolicy(Policy.MAXIMUM_SIZE)
        .setPolicyArgument(1000)
        .setCacheType(CacheType.MEMORY) //Memory works Datastore fails
        .build();
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
        String searchDB = ipCache.get(searchIP);

        if (searchDB != null) {
            if(searchDB.equalsIgnoreCase(Flagged.TRUE))
            {
                data.flagged = Flagged.TRUE;
            }
            else if(searchDB.equalsIgnoreCase(Flagged.FALSE)){
                data.flagged = Flagged.FALSE;
            }
        }
        else{
            try {
                result = Unirest.get("https://signals.api.auth0.com/badip/" + searchIP)
                        .header("X-Auth-Token", AUTH0_API_KEY)
                        .asString();
           
                if(result.getBody().equalsIgnoreCase(FLAGGED_FALSE))
                {
                    data.flagged = Flagged.FALSE;
                    ipCache.put(searchIP,data.flagged);
                }
                else if(result.getBody().equalsIgnoreCase(FLAGGED_TRUE))
                {
                    data.flagged = Flagged.TRUE;
                    ipCache.put(searchIP,data.flagged);
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