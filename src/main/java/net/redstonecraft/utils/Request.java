package net.redstonecraft.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Request {

    private final String url;
    private HttpURLConnection con;

    public Request(String url) {
        this.url = url;
    }

    public void connect() {
        try {
            URL urlUrl = new URL(url);
            con = (HttpURLConnection) urlUrl.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHeader(String key, String value) {
        con.addRequestProperty(key, value);
    }

    public void post(String content) {
        try {
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Content-Length", Integer.toString(contentBytes.length));
            OutputStream requestStream = con.getOutputStream();
            requestStream.write(contentBytes, 0, contentBytes.length);
            requestStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResponse() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        con.disconnect();
    }

}
