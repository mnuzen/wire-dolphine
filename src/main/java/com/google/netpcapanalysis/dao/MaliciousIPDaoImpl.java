package com.google.netpcapanalysis.dao;
import com.google.netpcapanalysis.dao.PCAPDaoImpl;
import com.google.netpcapanalysis.interfaces.dao.PCAPDao;
import com.google.netpcapanalysis.interfaces.dao.MaliciousIPDao;
import com.google.netpcapanalysis.models.PCAPdata;
import com.google.netpcapanalysis.models.MaliciousPacket;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;

import java.io.IOException;

import javax.lang.model.util.ElementScanner6;

public class  MaliciousIPDaoImpl implements MaliciousIPDao{
    private static final String AUTH0_API_KEY = "replace_api_key";

    private static final String FLAGGED_FALSE = "Resource not found";
    private static final String FLAGGED_TRUE = "200: OK";
    private static final String REQUEST_LIMIT = "Rate limit exceeded";

    private PCAPDao ipCache = new PCAPDaoImpl();

    public MaliciousIPDaoImpl(){
    }
    public PCAPdata isMalicious(PCAPdata data)
    {
        HttpResponse<String> result;

        if(ipCache.searchMaliciousDB(data.destination) == true)
        {
            data.flagged = true;
        }
        else{
            try {
                result = Unirest.get("https://signals.api.auth0.com/badip/" + data.destination)
                        .header("X-Auth-Token", AUTH0_API_KEY)
                        .asString();
           
                if(result.getBody().equals(FLAGGED_FALSE))
                {
                    data.flagged = false;
                }
                else if(result.getBody().equals(FLAGGED_TRUE))
                {
                    data.flagged = true; 
                    MaliciousPacket tempPacket = new MaliciousPacket(data.destination,data.flagged);
                    ipCache.setMaliciousIPObjects(tempPacket);
                }
                else{
                    System.out.println("Error request limit reached");
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