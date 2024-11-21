package onos.multivpn;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

/**
 * A client for interacting with the ONOS REST API.
 */
public class OnosAPIClient {

    // Base URL for ONOS REST API
    private static final String API_URL = "http://localhost:8181/onos/v1/";

    // Connects to ONOS to retrieve information based on the provided API endpoint.
    // endpoint : query (e.g., "hosts", "devices", "links")
    // return Raw JSON response from ONOS or an error message.
    public String connectToOnos(String endpoint) {
        try {
            // Construct the full URL for the ONOS API endpoint
            URL url = new URL(API_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " +
                    Base64.getEncoder().encodeToString("onos:rocks".getBytes()));

            // Get ONOS response
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) { // Success
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                return content.toString(); // Return raw JSON response
            } else {
                return "Failed to connect to ONOS. Response code: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error connecting to ONOS: " + e.getMessage();
        }
    }
    // get all devices
    public String getDevices() throws IOException {
        return connectToOnos("devices");
    }

    // get all hosts
    public String getHosts() throws IOException {
        return connectToOnos("hosts");
    }

    // get all links
    public String getLinks() throws IOException {
        return connectToOnos("links");
    }
}
