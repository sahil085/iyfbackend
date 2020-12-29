package com.iyfbackend.api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class SmsService {

    @Value("${api.key}")
    private String apikey;

    @Value("${sender.id}")
    private String senderId;

    public void sendSMS(String message, String contactNumbers) throws IOException {
        // Create a neat value object to hold the URL
        String apiKey = "apikey=" + apikey;
        String messageParam = "&message=" + message;
        String sender = "&senderid=" + senderId;
        String numbers = "&number=" + contactNumbers;
        HttpURLConnection conn = (HttpURLConnection) new URL("http://136.243.76.205/vb/apikey.php?").openConnection();
        String data = apiKey + sender + numbers + messageParam;
        conn.setDoOutput(true);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
        conn.getOutputStream().write(data.getBytes("UTF-8"));
// Open a connection(?) on the URL(??) and cast the response(???)
// This line makes the request

        final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        final StringBuffer stringBuffer = new StringBuffer();
        String line;
        while ((line = rd.readLine()) != null) {
            stringBuffer.append(line);
        }
        rd.close();

        System.out.println(stringBuffer.toString());
    }
}
