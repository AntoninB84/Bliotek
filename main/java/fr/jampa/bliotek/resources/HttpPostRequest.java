package fr.jampa.bliotek.resources;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import fr.jampa.bliotek.R;

/**
 *  Generic AsyncTask class made to handle HttpRequests
 *
 * strings array => [0] Server URL to request handling file
 *                  [1] Params
 *                  [2] Type of request ( POST / GET )
 *                  [3] Params encoding ( Json / url-encoded )
 *                  -> For some reason, could not get to pass JSON arrays
 *
 * **/

public abstract class HttpPostRequest extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;

            try {
                StringBuffer response = null;
                URL url = new URL(strings[0]);

                // Init connection to server
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestProperty("Content-Type", strings[3]);

                //connection.setUseCaches(false);
                if (strings[2] == "POST") {
                    String infos = strings[1];
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");

                    OutputStream output = new BufferedOutputStream(connection.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
                    writer.write(infos); // The params are transmitted here
                    writer.flush();
                    writer.close();
                }

                // Handle response
                int responseCode = connection.getResponseCode();
                switch (responseCode) {
                    case 200:
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        return response.toString();
                    default:
                        Log.i("test", "Code != 200 : " + String.valueOf(responseCode));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return "Erreur";
            } finally {

                //Closing connection
                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        return null;
    }
}
