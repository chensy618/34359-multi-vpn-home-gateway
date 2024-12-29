package onos.multivpn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A client for interacting with the ONOS REST API.
 */
public class OnosAPIClient {

    private static final String API_URL = "http://localhost:8181/onos/v1/";
    private static final String AUTH_HEADER = "Basic " + Base64.getEncoder().encodeToString("onos:rocks".getBytes());

    // Connects to ONOS to retrieve information based on the provided API endpoint
    public String connectToOnos(String endpoint) {
        try {
            URL url = new URL(API_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", AUTH_HEADER);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder content = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    return content.toString();
                }
            } else {
                return "Failed to connect to ONOS. Response code: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error connecting to ONOS: " + e.getMessage();
        }
    }

    public String getHosts() {
        return connectToOnos("hosts");
    }

    public String getCluster() {
        return connectToOnos("cluster");
    }

    public String getLinks() {
        return connectToOnos("links");
    }

    public String getNetworkConfiguration() {
        return connectToOnos("network/configuration");
    }

    public String getTopology() {
        return connectToOnos("topology");
    }

    // Push VLAN tag for a host
    public void pushVlan(String mac, String vlanId, String ipAddress, String elementId, String port) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("mac", mac);
            payload.put("vlan", vlanId);
            payload.put("ipAddresses", new JSONArray().put(ipAddress));
            payload.put("configured","false");

            JSONObject location = new JSONObject();
            location.put("elementId", elementId);
            location.put("port", port);
            payload.put("locations", new JSONArray().put(location));

            URL url = new URL(API_URL + "hosts");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", AUTH_HEADER);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.toString().getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                System.out.println("VLAN tag " + vlanId + " pushed successfully for host " + mac);
            } else {
                System.out.println("Failed to push VLAN tag for host " + mac + ". Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
