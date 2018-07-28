package com.example.pyros.daikin_controller;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;


public class DaikinHTTPController
{

    public void sendParams(DaikinModel params)
    {
        String url = "http://192.168.1.15/aircon/set_control_info";

        String urlParams = "";
        urlParams += "pow=" + params.status;
        urlParams += "&mode=" + params.mode;
        urlParams += "&stemp=" + (int)params.targetTemp;
        urlParams += "&shum=" + (int)params.targetHumidity;
        urlParams += "&f_rate=" + params.fanRate;
        urlParams += "&f_dir=" + params.fanDir;

        sendPost(url +"?" + urlParams, "");
    }

    public void getParams(DaikinModel params)
    {
        String url = "http://192.168.1.15/aircon/get_control_info";

        String response = sendGet(url);

        StringTokenizer tokenizer = new StringTokenizer(response, ",");
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextElement().toString();

            StringTokenizer tokenizer1 = new StringTokenizer(token, "=");
            String paramName = tokenizer1.nextElement().toString();
            String paramValue = tokenizer1.hasMoreElements() ? tokenizer1.nextElement().toString() : "";
            try {
                switch (paramName) {
                    case "pow":
                        params.setStatus(paramValue);
                        break;
                    case "mode":
                        params.setMode(paramValue);
                        break;
                    case "stemp":
                        params.targetTemp = Float.parseFloat(paramValue);
                        break;
                    case "shum":
                        params.targetHumidity = Float.parseFloat(paramValue);
                        break;
                    case "f_rate":
                        params.setFanRate(paramValue);
                        break;
                    case "f_dir":
                        params.setFanDir(paramValue);
                        break;
                    case "b_stemp":
                        params.currentTemp = Float.parseFloat(paramValue);
                        break;
                }
            } catch (Exception e) {
                Log.e("getParams()", e.getMessage());
            }
        }
    }

    private String sendPost(String targetURL, String urlParameters)
    {
        URL url;
        HttpURLConnection connection = null;

        try
        {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
            } else {
                Log.e("sendGet", "POST Error:" + responseCode);
            }

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    private String sendGet(String url)
    {

        HttpURLConnection connection = null;
        StringBuffer response = new StringBuffer();
        try
        {
            URL obj = new URL(url);
            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } else {
                Log.e("sendGet", "GET Error:" + responseCode);
            }

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response.toString();
    }

}
