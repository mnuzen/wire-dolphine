package com.google.sps.lookup_ip;
import com.google.sps.datastore.PCAPdata;
import com.google.sps.lookup_ip.MaliciousIPDao;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;

public class  MaliciousIPDao {

    public PCAPdata isMalicious(PCAPdata data)
    {
        HttpResponse<String> result;
        try {
            //result = Unirest.get("https://signals.api.auth0.com/v2.0/ip/" + data.source) // 10 hits full data
            result = Unirest.get("https://signals.api.auth0.com/badip/" + data.destination) //1 hit just flagged
                    //.header("X-Auth-Token", "API_KEY")   //1 hit url will return results without API key
                    .asString();

            //if result.getBody(); = "Resource not found"  IP not in block list;        
            if(result.getBody().equals("Resource not found"))
            {
                data.flagged = false;
            }
            //if result.getBody(); = "200: OK" IP is in block list
            else if(result.getBody().equals("200: OK"))
            {
                data.flagged = true;
            }
            System.out.println("Web results: " + result.getBody());
            System.out.println("PCAP File: " +data.flagged);

        } catch (UnirestException e) {
            System.out.println("IP lookup failed");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return data;
    }
    
}