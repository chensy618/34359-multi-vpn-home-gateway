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
import static java.lang.System.*;

/**
 * A client for interacting with the ONOS REST API.
 */
public class OnosAPIClient {

    // Base URL for ONOS REST API
    private static final String API_URL = "http://localhost:8181/onos/v1/";
    private static final String AUTH_HEADER = "Basic " + Base64.getEncoder().encodeToString("onos:rocks".getBytes());

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
        return response;
    }

    // get all hosts
    public String getHosts() throws IOException {
        String response = connectToOnos("hosts");
        return response;
    }

    // get all links
    public String getLinks() throws IOException {
        String response = connectToOnos("links");
        return response;
    }

    // Get all flow entries
    public String getFlows() throws IOException {
        String response = connectToOnos("flows");
        return response;
    }

    public int submitFlowRule(JSONObject flowJson, String deviceId, String appId) {
        try {
            URL url = new URL(API_URL + "flows/" + deviceId + "?appId=" + appId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", AUTH_HEADER);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(flowJson.toString().getBytes());
                os.flush();
            }

            out.println(flowJson);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                System.out.println("Flow rule added successfully to device " + deviceId);
            } else {
                System.out.println("Failed to add flow rule to device " + deviceId + ". Response code: " + responseCode);
            }
            return responseCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public JSONObject createFlowRule(String inPort, String outPort, String ethSrc, String ethDst, String vlanOp, String vlanId) {
        JSONObject flow = new JSONObject();
        flow.put("priority", "40000");
        flow.put("isPermanent", true);

        // Treatment (actions)
        JSONObject treatment = new JSONObject();
        JSONArray instructions = new JSONArray();

        // VLAN operations
        if ("push".equalsIgnoreCase(vlanOp)) {
            instructions.put(new JSONObject().put("type", "L2MODIFICATION").put("subtype", "VLAN_PUSH"));
            instructions.put(new JSONObject().put("type", "L2MODIFICATION").put("subtype", "VLAN_ID").put("vlanId", vlanId));
        } else if ("pop".equalsIgnoreCase(vlanOp)) {
            instructions.put(new JSONObject().put("type", "L2MODIFICATION").put("subtype", "VLAN_POP"));
        }

        // Output action
        instructions.put(new JSONObject().put("type", "OUTPUT").put("port", outPort));
        treatment.put("instructions", instructions);
        flow.put("treatment", treatment);

        // Selector (match conditions)
        JSONObject selector = new JSONObject();
        JSONArray criteria = new JSONArray();
        criteria.put(new JSONObject().put("type", "ETH_TYPE").put("ethType", "0x800")); // IPv4
        criteria.put(new JSONObject().put("type", "IN_PORT").put("port", inPort));
        criteria.put(new JSONObject().put("type", "ETH_SRC").put("mac", ethSrc));
        criteria.put(new JSONObject().put("type", "ETH_DST").put("mac", ethDst));
        if (vlanId != null && !"".equals(vlanId) && !"push".equalsIgnoreCase(vlanOp)) {
            criteria.put(new JSONObject().put("type", "VLAN_VID").put("vlanId", vlanId));
        }
        selector.put("criteria", criteria);
        flow.put("selector", selector);
        return flow;
    }

    public void addFlowRule(String deviceId, String inPort, String mac, String vlanId, String outPort, String actions) {
        try {
            // Construct flow JSON
            JSONObject flow = new JSONObject();
            flow.put("isPermanent", true);
            flow.put("deviceId", deviceId);

            // Treatment (actions)
            JSONObject treatment = new JSONObject();
            JSONArray instructions = new JSONArray();

            // Add OUTPUT action (output port)
            if (outPort != null) {
                instructions.put(new JSONObject().put("type", "OUTPUT").put("port", outPort));
            }

            // Parse additional actions
            if (actions != null && !actions.isEmpty()) {
                String[] actionArray = actions.split(",");
                for (String action : actionArray) {
                    if (action.startsWith("VLAN_PUSH")) {
                        String pushVlanId = action.split(":")[1];
                        instructions.put(new JSONObject().put("type", "L2MODIFICATION").put("subtype", "VLAN_PUSH").put("vlanId", pushVlanId));
                    } else if (action.equals("VLAN_POP")) {
                        instructions.put(new JSONObject().put("type", "L2MODIFICATION").put("subtype", "VLAN_POP"));
                    }
                }
            }
            treatment.put("instructions", instructions);
            flow.put("treatment", treatment);

            // Selector (match conditions)
            JSONObject selector = new JSONObject();
            JSONArray criteria = new JSONArray();
            criteria.put(new JSONObject().put("type", "IN_PORT").put("port", inPort));
            criteria.put(new JSONObject().put("type", "ETH_DST").put("mac", mac));
            if (vlanId != null) {
                criteria.put(new JSONObject().put("type", "VLAN_VID").put("vlanId", vlanId));
            }
            selector.put("criteria", criteria);
            flow.put("selector", selector);

            // Send POST request to ONOS
            URL url = new URL(API_URL + "flows/" + deviceId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", AUTH_HEADER);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            out.println("Generated JSON for flow rule: " + flow.toString());

            try (OutputStream os = conn.getOutputStream()) {
                os.write(flow.toString().getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                out.println("Flow rule added successfully to device " + deviceId);
            } else {
                out.println("Failed to add flow rule to device " + deviceId + ". Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void clearAllFlows(String appId) {
        try {
            // 构建删除流表的 URL
            String urlString = API_URL + "flows/application/" + appId;
            URL url = new URL(urlString);

            // 创建 HTTP DELETE 请求
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", AUTH_HEADER);

            // 获取响应码
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Successfully cleared all flows for appId: " + appId);
            } else {
                System.out.println("Failed to clear flows for appId: " + appId + ". Response code: " + responseCode);
            }

            // 关闭连接
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error clearing flows for appId: " + appId + ". Exception: " + e.getMessage());
        }
    }
    /**
     * Submits an intent to the ONOS controller.
     *
     * @param appId   The application ID (e.g., "org.onosproject.ovsdb").
     * @param type    The type of intent (e.g., "HostToHostIntent").
     * @param priority The priority of the intent (e.g., 55).
     * @param one     The first host ID (e.g., "00:00:00:00:00:01/None").
     * @param two     The second host ID (e.g., "00:00:00:00:00:02/None").
     * @return True if the intent is submitted successfully, false otherwise.
     */
    public boolean submitIntent(String appId, String type, String priority, String one, String two) {
        try {
            // Construct the JSON payload for the intent
            JSONObject intentPayload = new JSONObject();
            intentPayload.put("type", type);
            intentPayload.put("appId", appId);
            intentPayload.put("priority", priority);
            intentPayload.put("one", one);
            intentPayload.put("two", two);

            // Construct the URL for the intents API
            URL url = new URL(API_URL + "intents");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", AUTH_HEADER);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send the JSON payload
            try (OutputStream os = conn.getOutputStream()) {
                os.write(intentPayload.toString().getBytes());
                os.flush();
            }

            // Check the response code
            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                System.out.println("Intent submitted successfully.");
                return true;
            } else {
                System.out.println("Failed to submit intent. Response code: " + responseCode);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getIntents() {
        try {
            // Construct the URL for the intents endpoint
            URL url = new URL(API_URL + "intents");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", AUTH_HEADER);
            conn.setRequestProperty("Accept", "application/json");

            // Check the response code
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) { // Success
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return response.toString();
            } else {
                return "Failed to retrieve intents. Response code: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error retrieving intents: " + e.getMessage();
        }
    }
    public boolean deleteIntent(String appId, String key) {
        try {
            // Construct the URL for deleting the intent
            String urlString = API_URL + "intents/" + appId + "/" + key;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", AUTH_HEADER);
            conn.setRequestProperty("Accept", "application/json");

            // Get the response code
            int responseCode = conn.getResponseCode();
            if (responseCode == 204) { // Successful deletion (No Content)
                System.out.println("Intent deleted successfully: " + key);
                return true;
            } else {
                System.out.println("Failed to delete intent. Response code: " + responseCode);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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


}
