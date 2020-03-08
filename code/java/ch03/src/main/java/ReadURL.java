import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ReadURL {

    public String readUrl(String urlstring) {
        StringBuffer sb = new StringBuffer();
        try {
            URL url = new URL(urlstring);
            URLConnection urlConnection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine);
            in.close();
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }
        return sb.toString();
    }

    public JSONObject stringToJSON(String rawjson) {
        return new JSONObject(rawjson);
    }

    public static void main(String[] args) throws Exception {
        String apikey = "Add your key here....";
        ReadURL r = new ReadURL();
        String rawstring =r.readUrl("https://api.openweathermap.org/data/2.5/weather?q=London&APPID=" + apikey);
        JSONObject j = r.stringToJSON(rawstring);
        System.out.println(j.toString());
    }
}

