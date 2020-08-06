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

public class  MaliciousIPDaoImpl implements MaliciousIPDao{
    private static final String AUTH0_API_KEY = "replace_api_key";

    private static final String FLAGGED_FALSE = "Resource not found";
    private static final String FLAGGED_TRUE = "200: OK";

    private PCAPDao ipCache = new PCAPDaoImpl();

    public MaliciousIPDaoImpl(){
    }
    public PCAPdata isMalicious(PCAPdata data)
    {
        HttpResponse<String> result;

        if(ipCache.searchMaliciousDB(data.destination) == true)
        {
            data.flagged = Flagged.TRUE;
        }
        else{
            try {
                result = Unirest.get("https://signals.api.auth0.com/badip/" + data.destination)
                        .header("X-Auth-Token", AUTH0_API_KEY)
                        .asString();
           
                if(result.getBody().equals(FLAGGED_FALSE))
                {
                    data.flagged = Flagged.FALSE;
                }
                else if(result.getBody().equals(FLAGGED_TRUE))
                {
                    data.flagged = Flagged.TRUE; 
                    MaliciousPacket tempPacket = new MaliciousPacket(data.destination,data.flagged);
                    ipCache.setMaliciousIPObjects(tempPacket);
                }
                else{
                    data.flagged = Flagged.UNKNOWN; 
                }
    
            } catch (UnirestException e) {
                System.out.println("IP lookup failed");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return data;
    }
    
}