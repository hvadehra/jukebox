package org.melocine.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 9/11/14
 * Time: 1:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class URLReader {

    public String getUrl(String url){
        System.err.println("Fetching from: " + url);
        String output = "";
        try{
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Chrome 1.0");
            connection.setRequestProperty("Accept", "*/*");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                output = output + inputLine;
            }
            in.close();
        }
        catch(Exception e){
            System.err.println("Could not get url: " + e.getMessage());
        }
        return output;
    }
}
