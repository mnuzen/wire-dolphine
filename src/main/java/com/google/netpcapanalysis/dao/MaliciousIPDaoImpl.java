package com.google.netpcapanalysis.dao;

import com.google.netpcapanalysis.interfaces.dao.MaliciousIPDao;
import com.google.netpcapanalysis.models.PCAPdata;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;

import javax.lang.model.util.ElementScanner6;

public class  MaliciousIPDaoImpl implements MaliciousIPDao{
    private static final String FLAGGED_FALSE = "Resource not found";
    private static final String FLAGGED_TRUE = "200: OK";
    private static final String REQUEST_LIMIT = "Rate limit exceeded.Please reduce your hits per minute.";

    public MaliciousIPDaoImpl(){

    }
    public PCAPdata isMalicious(PCAPdata data)
    {
        HttpResponse<String> result;
        try {
            result = Unirest.get("https://signals.api.auth0.com/badip/" + data.destination)
                    .header("X-Auth-Token", "API_KEY")
                    .asString();
       
            if(result.getBody().equals(FLAGGED_FALSE))
            {
                data.flagged = false;
            }
            else if(result.getBody().equals(FLAGGED_TRUE))
            {
                data.flagged = true;
            }
            else if(result.getBody().equals(REQUEST_LIMIT))
            {//documentations doesnt list what per minute limit is
                data.flagged = false;
            }

        } catch (UnirestException e) {
            System.out.println("IP lookup failed");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return data;
    }
    
}