package example.com.maps;

/**
 * Created by Android1 on 7/21/2015.
 */
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class Http {

    public final static String TAG = Http.class.getName();

    public String read(String httpUrl) throws IOException {
        String httpData = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            Log.i(TAG, "READING...");
            URL url = new URL(httpUrl);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();
            Log.i(TAG, "URL: " + url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            Log.i(TAG, "URL RESULT CODE: " + httpURLConnection.getResponseCode());
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            httpData = stringBuffer.toString();
            bufferedReader.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        Log.i(TAG, "HTTP Data: " + httpData.toString());
        return httpData;
    }
}