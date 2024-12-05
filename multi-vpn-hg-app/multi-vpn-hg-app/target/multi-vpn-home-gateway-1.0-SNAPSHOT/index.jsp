<%@ page import="org.json.JSONObject" %>
<%@ page import="org.json.JSONArray" %><%--
  Created by IntelliJ IDEA.
  User: student
  Date: 11/8/24
  Time: 8:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<html>--%>
<%--<head>--%>
<%--    <title>Multi-VPN Home Gateway</title>--%>
<%--</head>--%>
<%--<body>--%>
<%--    <h1>Multi-VPN Home Gateway</h1>--%>
<%--    <!-- Node Selection(Service Provider) -->--%>
<%--    <h2>Select Service Provider Nodes</h2>--%>
<%--&lt;%&ndash;    <label for="serviceProvider">Choose Provider:</label>--%>
<%--    <select name="serviceProvider" id="serviceProvider">--%>
<%--        <option value="provider1">Provider 1</option>--%>
<%--        <option value="provider2">Provider 2</option>--%>
<%--        <!-- Add other providers here -->--%>
<%--    </select>&ndash;%&gt;--%>
<%--    <!-- Checkbox will be better-->--%>
<%--    <form action="SelectVPNNodeServlet" method="post">--%>
<%--        <table border="1" cellpadding="10">--%>
<%--            <tr>--%>
<%--                <th>Nodes</th>--%>
<%--                <th>Select</th>--%>
<%--            </tr>--%>
<%--            <tr>--%>
<%--                <td>Service Provider 1 (SP1)</td>--%>
<%--                <td><input type="radio" name="serviceProvider" value="provider1"></td>--%>
<%--            </tr>--%>
<%--            <tr>--%>
<%--                <td>Service Provider 2 (SP2)</td>--%>
<%--                <td><input type="radio" name="serviceProvider" value="provider2"></td>--%>
<%--            </tr>--%>
<%--            <tr>--%>
<%--                <td>Service Provider 3 (SP3)</td>--%>
<%--                <td><input type="radio" name="serviceProvider" value="provider3"></td>--%>
<%--            </tr>--%>
<%--        </table>--%>
<%--        <br><br>--%>

<%--        <!-- Optional (Automatic Route) -->--%>
<%--        <input type="checkbox" id="autoRoute" name="autoRoute">--%>
<%--        <span>Enable Automatic Routing</span>--%>
<%--        <br><br>--%>

<%--        <!-- Submit button -->--%>
<%--        <input type="submit" value="Submit">--%>
<%--    </form>--%>
<%--    <!-- Network Condition Display -->--%>
<%--    <h2>Network Condition</h2>--%>
<%--    <h3>Node status</h3>--%>
<%--    <div id="networkCondition">--%>
<%--        <%--%>
<%--            String onosResponse = (String) request.getAttribute("onosResponse");--%>
<%--            if (onosResponse != null) {--%>
<%--                try {--%>
<%--                    // Parse JSON response--%>
<%--                    JSONObject jsonResponse = new JSONObject(onosResponse);--%>
<%--                    JSONArray hosts = jsonResponse.getJSONArray("hosts");--%>

<%--                    if (hosts.length() > 0) {--%>
<%--        %>--%>
<%--        <table border="1" cellpadding="10">--%>
<%--            <tr>--%>
<%--                <th>Host ID</th>--%>
<%--                <th>MAC Address</th>--%>
<%--                <th>IP Address</th>--%>
<%--                <th>Switch ID</th>--%>
<%--                <th>Port</th>--%>
<%--            </tr>--%>
<%--            <%--%>
<%--                for (int i = 0; i < hosts.length(); i++) {--%>
<%--                    JSONObject host = hosts.getJSONObject(i);--%>
<%--                    String hostId = host.optString("id", "Unknown");--%>
<%--                    String mac = host.optString("mac", "Unknown");--%>
<%--                    JSONArray ipAddresses = host.optJSONArray("ipAddresses");--%>
<%--                    String ip = ipAddresses != null && ipAddresses.length() > 0 ? ipAddresses.getString(0) : "Unknown";--%>
<%--                    JSONArray locations = host.optJSONArray("locations");--%>
<%--                    String switchId = locations != null && locations.length() > 0 ? locations.getJSONObject(0).optString("elementId", "Unknown") : "Unknown";--%>
<%--                    String port = locations != null && locations.length() > 0 ? locations.getJSONObject(0).optString("port", "Unknown") : "Unknown";--%>
<%--            %>--%>
<%--            <tr>--%>
<%--                <td><%= hostId %></td>--%>
<%--                <td><%= mac %></td>--%>
<%--                <td><%= ip %></td>--%>
<%--                <td><%= switchId %></td>--%>
<%--                <td><%= port %></td>--%>
<%--            </tr>--%>
<%--            <%--%>
<%--                }--%>
<%--            %>--%>
<%--            </table>--%>
<%--            <%--%>
<%--                    } else {--%>
<%--                        out.println("<p>No hosts available in ONOS response.</p>");--%>
<%--                    }--%>
<%--                } catch (Exception e) {--%>
<%--                        out.println("<p>Error parsing ONOS response: " + e.getMessage() + "</p>");--%>
<%--                    e.printStackTrace();--%>
<%--                }--%>
<%--            %>--%>
<%--        <%--%>
<%--        } else {--%>
<%--        %>--%>
<%--        <p>No response from ONOS.</p>--%>
<%--        <%--%>
<%--            }--%>
<%--        %>--%>
<%--    </div>--%>

<%--    <!-- Selected VPN Node Information -->--%>
<%--    <h2>Selected VPN Node</h2>--%>
<%--    <div id="selectedVPNNode">--%>
<%--        <!-- Display the VPN node chosen above -->--%>
<%--        <%--%>
<%--            String[] selectedNodes = (String[]) request.getAttribute("selectedNodes");--%>
<%--            Boolean autoRoute = (Boolean) request.getAttribute("autoRoute");--%>
<%--            String autoRouteMessage = (String) request.getAttribute("autoRouteMessage");--%>

<%--            if (selectedNodes != null && selectedNodes.length > 0) {--%>
<%--        %>--%>
<%--            <ul>--%>
<%--                <% for (String node : selectedNodes) { %>--%>
<%--                <li><%= node %></li>--%>
<%--                <% } %>--%>
<%--            </ul>--%>
<%--            <p>Automatic Routing Enabled: <%= autoRoute %></p>--%>

<%--            <% if (autoRouteMessage != null) { %>--%>
<%--                    <p><%= autoRouteMessage %></p>--%>
<%--            <% } %>--%>
<%--        <%--%>
<%--            } else {--%>
<%--        %>--%>
<%--            <p>No service provider selected.</p>--%>
<%--        <%--%>
<%--            }--%>
<%--        %>--%>
<%--    </div>--%>
<%--</body>--%>
<%--</html>--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Multi-VPN Home Gateway</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            color: #333;
            margin: 0;
            padding: 0;
        }

        h1, h2, h3 {
            color: #1a73e8;
        }

        h1 {
            text-align: center;
            margin-top: 20px;
        }

        h2 {
            border-bottom: 2px solid #1a73e8;
            padding-bottom: 5px;
        }

        form {
            margin: 20px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            max-width: 600px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        table th, table td {
            padding: 10px;
            text-align: left;
            border: 1px solid #ddd;
        }

        table th {
            background-color: #1a73e8;
            color: white;
        }

        input[type="radio"], input[type="checkbox"] {
            transform: scale(1.2);
            margin-right: 10px;
        }

        input[type="submit"] {
            background-color: #1a73e8;
            color: white;
            border: none;
            border-radius: 5px;
            padding: 10px 20px;
            font-size: 16px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #1667c2;
        }

        .container {
            margin: 20px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            max-width: 800px;
        }

        ul {
            list-style: none;
            padding: 0;
        }

        ul li {
            padding: 5px 0;
        }

        .center {
            text-align: center;
        }

        .error {
            color: red;
            font-weight: bold;
        }

        .success {
            color: green;
            font-weight: bold;
        }

        .selected-provider {
            background-color: #e8f4ff;
            padding: 15px;
            border: 1px solid #1a73e8;
            border-radius: 8px;
            margin-top: 10px;
        }

        .selected-provider h3 {
            margin: 0;
            color: #1a73e8;
        }

        .selected-provider p {
            margin: 5px 0;
            color: #333;
        }
    </style>
</head>
<body>
<h1>Multi-VPN Home Gateway Application</h1>
<div class="container">
    <!-- Node Selection (Service Provider) -->
    <h2>VPN Node Selection</h2>
    <form action="SelectVPNNodeServlet" method="post">
        <table>
            <tr>
                <th>Nodes</th>
                <th>Select</th>
            </tr>
            <tr>
                <td>Service Provider 1 (SP1)</td>
                <td><input type="radio" name="serviceProvider" value="provider1"></td>
            </tr>
            <tr>
                <td>Service Provider 2 (SP2)</td>
                <td><input type="radio" name="serviceProvider" value="provider2"></td>
            </tr>
            <tr>
                <td>Service Provider 3 (SP3)</td>
                <td><input type="radio" name="serviceProvider" value="provider3"></td>
            </tr>
        </table>
        <br>
        <input type="checkbox" id="autoRoute" name="autoRoute">
        <label for="autoRoute">Enable Automatic Routing</label>
        <br><br>
        <div class="center">
            <input type="submit" value="Submit">
        </div>
    </form>
</div>

<div class="container">
    <!-- Network Condition Display -->
    <h2>Network Topology</h2>
    <h3>Node Status</h3>
    <div id="networkCondition">
        <%
            String onosResponse = (String) request.getAttribute("onosResponse");
            if (onosResponse != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(onosResponse);
                    JSONArray hosts = jsonResponse.getJSONArray("hosts");

                    if (hosts.length() > 0) {
        %>
        <table>
            <tr>
                <th>Host ID</th>
                <th>MAC Address</th>
                <th>IP Address</th>
                <th>Switch ID</th>
                <th>Port</th>
            </tr>
            <%
                for (int i = 0; i < hosts.length(); i++) {
                    JSONObject host = hosts.getJSONObject(i);
                    String hostId = host.optString("id", "Unknown");
                    String mac = host.optString("mac", "Unknown");
                    JSONArray ipAddresses = host.optJSONArray("ipAddresses");
                    String ip = ipAddresses != null && ipAddresses.length() > 0 ? ipAddresses.getString(0) : "Unknown";
                    JSONArray locations = host.optJSONArray("locations");
                    String switchId = locations != null && locations.length() > 0 ? locations.getJSONObject(0).optString("elementId", "Unknown") : "Unknown";
                    String port = locations != null && locations.length() > 0 ? locations.getJSONObject(0).optString("port", "Unknown") : "Unknown";
            %>
            <tr>
                <td><%= hostId %></td>
                <td><%= mac %></td>
                <td><%= ip %></td>
                <td><%= switchId %></td>
                <td><%= port %></td>
            </tr>
            <%
                }
            %>
        </table>
        <%
                } else {
                    out.println("<p class='error'>No hosts available in ONOS response.</p>");
                }
            } catch (Exception e) {
                out.println("<p class='error'>Error parsing ONOS response: " + e.getMessage() + "</p>");
            }
        } else {
        %>
        <p class="error">No response from ONOS.</p>
        <%
            }
        %>
    </div>
</div>

<div class="container">
    <!-- Selected VPN Node Information -->
    <h2>Selected VPN Node</h2>
    <div id="selectedVPNNode">
        <%
            String[] selectedNodes = (String[]) request.getAttribute("selectedNodes");
            Boolean autoRoute = (Boolean) request.getAttribute("autoRoute");
            String autoRouteMessage = (String) request.getAttribute("autoRouteMessage");
            String destination = "h2";
            if (selectedNodes != null && selectedNodes.length > 0) {
                if ("provider1".equals(selectedNodes[0])) {
                    destination = "h2";
                } else if ("provider2".equals(selectedNodes[0])) {
                    destination = "h3";
                } else if ("provider3".equals(selectedNodes[0])) {
                    destination = "h4";
                } else {
                    destination = "Unknown";
                }

        %>
        <div class="selected-provider">
            <h3>Selected Provider: <%= selectedNodes[0] %></h3>
            <p>Destination: <strong><%= destination %></strong></p>
            <p>Automatic Routing Enabled: <strong><%= autoRoute != null && autoRoute ? "Yes" : "No" %></strong></p>
            <% if (autoRouteMessage != null) { %>
            <p class="success"><%= autoRouteMessage %></p>
            <% } %>
        </div>
        <%
        } else {
        %>
        <p class="error">No service provider selected.</p>
        <%
            }
        %>
    </div>
</div>
</body>
</html>
