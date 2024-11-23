package onos.multivpn;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;


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
        String response = connectToOnos("devices");
        System.out.println("Device JSON" + response);
        return response;
    }

    // get all hosts
    public String getHosts() throws IOException {
        String response = connectToOnos("hosts");
        System.out.println("Host JSON" + response);
        return response;
    }

    // get all links
    public String getLinks() throws IOException {
        String response = connectToOnos("links");
        System.out.println("Links JSON" + response);
        return response;
    }

    public String extractDeviceId(String devicesJson) {
        System.out.println("Extracting device ID from JSON: " + devicesJson);
        JSONObject json = new JSONObject(devicesJson);
        JSONArray devices = json.getJSONArray("devices");
        for (int i = 0; i < devices.length(); i++) {
            JSONObject device = devices.getJSONObject(i);
            System.out.println("Device: " + device);
            if (device.getBoolean("available")) { // 找到可用设备
                System.out.println("Available Device ID: " + device.getString("id"));
                return device.getString("id");
            }
        }
        System.out.println("No available device found.");
        return null;
    }

    // Processes topology data and assigns VLANs based on host-switch relationships
    public Map<Integer, String[][]> processTopologyAndAssignVlans(String hostsJson) {
        Map<String, Integer> switchVlanMap = new HashMap<>();
        Map<Integer, String[][]> vlanHostMap = new HashMap<>();
        int vlanCounter = 10; // Starting VLAN ID

        JSONObject hostsData = new JSONObject(hostsJson);
        JSONArray hostsArray = hostsData.getJSONArray("hosts");

        // Process each host
        for (int i = 0; i < hostsArray.length(); i++) {
            JSONObject host = hostsArray.getJSONObject(i);
            String mac = host.getString("mac");
            String ip = host.getJSONArray("ipAddresses").getString(0);
            JSONObject location = host.getJSONArray("locations").getJSONObject(0);
            String switchId = location.getString("elementId");

            // Assign VLAN if not already assigned for this switch
            if (!switchVlanMap.containsKey(switchId)) {
                switchVlanMap.put(switchId, vlanCounter);
                vlanCounter += 10; // Increment VLAN ID for next switch
            }

            int vlanId = switchVlanMap.get(switchId);

            // Add host to VLAN map
            vlanHostMap.putIfAbsent(vlanId, new String[][]{});
            String[][] hostsForVlan = vlanHostMap.get(vlanId);

            String[][] updatedHosts = new String[hostsForVlan.length + 1][];
            System.arraycopy(hostsForVlan, 0, updatedHosts, 0, hostsForVlan.length);
            updatedHosts[hostsForVlan.length] = new String[]{mac, ip, switchId, String.valueOf(location.getInt("port"))};
            vlanHostMap.put(vlanId, updatedHosts);
        }

        return vlanHostMap;
    }

    public void configureHostsVlan(Map<Integer, String[][]> vlanHostMap) {
        for (Map.Entry<Integer, String[][]> entry : vlanHostMap.entrySet()) {
            int vlanId = entry.getKey();
            String[][] hosts = entry.getValue();

            for (String[] host : hosts) {
                String mac = host[0];
                String ip = host[1];
                String switchId = host[2];
                int port = Integer.parseInt(host[3]);
                //updateHostVlan(mac, vlanId, ip, switchId, port);
                // VLAN 参数为 -1 表示未配置
                deleteHost(mac, -1);

                // Re-add the host with updated VLAN
                addHostToVlan(mac, vlanId, ip, switchId, port);
            }
        }
    }

    private void deleteHost(String mac, int vlan) {
        try {
            URL url = new URL(API_URL + "hosts/" + mac + "/" + vlan);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Basic " +
                    Base64.getEncoder().encodeToString("onos:rocks".getBytes()));

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 204) {
                System.out.println("Host " + mac + " deleted successfully.");
            } else {
                System.out.println("Failed to delete host " + mac + ". Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addHostToVlan(String mac, int vlanId, String ip, String switchId, int port) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("mac", mac);
            payload.put("vlan", vlanId);
            payload.put("ipAddresses", new JSONArray().put(ip));

            JSONObject location = new JSONObject();
            location.put("elementId", switchId);
            location.put("port", port);
            payload.put("locations", new JSONArray().put(location));

            URL url = new URL(API_URL + "hosts");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Basic " +
                    Base64.getEncoder().encodeToString("onos:rocks".getBytes()));
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                System.out.println("Host " + mac + " added to VLAN " + vlanId + " successfully.");
            } else {
                System.out.println("Failed to add host " + mac + " to VLAN " + vlanId + ". Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    Updates VLAN configuration for an existing host
//    private void updateHostVlan(String mac, int vlanId, String ip, String switchId, int port) {
//        try {
//            JSONObject payload = new JSONObject();
//            payload.put("mac", mac);
//            payload.put("vlan", vlanId);
//            payload.put("ipAddresses", new JSONArray().put(ip));
//
//            JSONObject location = new JSONObject();
//            location.put("elementId", switchId);
//            location.put("port", port);
//            payload.put("locations", new JSONArray().put(location));
//
//            URL url = new URL(API_URL + "hosts/" + mac); // Use the specific host endpoint
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("PUT"); // Use PUT for updating existing resources
//            conn.setRequestProperty("Authorization", "Basic " +
//                    Base64.getEncoder().encodeToString("onos:rocks".getBytes()));
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setDoOutput(true);
//
//            try (OutputStream os = conn.getOutputStream()) {
//                os.write(payload.toString().getBytes());
//                os.flush();
//            }
//
//            int responseCode = conn.getResponseCode();
//            if (responseCode == 200 || responseCode == 201) {
//                System.out.println("Host " + mac + " updated to VLAN " + vlanId + " successfully.");
//            } else {
//                System.out.println("Failed to update host " + mac + " to VLAN " + vlanId +
//                        ". Response code: " + responseCode);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}
