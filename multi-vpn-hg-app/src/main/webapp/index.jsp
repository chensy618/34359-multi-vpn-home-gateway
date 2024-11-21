<%@ page import="org.json.JSONObject" %>
<%@ page import="org.json.JSONArray" %><%--
  Created by IntelliJ IDEA.
  User: student
  Date: 11/8/24
  Time: 8:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Multi-VPN Home Gateway</title>
</head>
<body>
    <h1>Multi-VPN Home Gateway</h1>
    <!-- Node Selection(Service Provider) -->
    <h2>Select Service Provider Nodes</h2>
<%--    <label for="serviceProvider">Choose Provider:</label>
    <select name="serviceProvider" id="serviceProvider">
        <option value="provider1">Provider 1</option>
        <option value="provider2">Provider 2</option>
        <!-- Add other providers here -->
    </select>--%>
    <!-- Checkbox will be better-->
    <form action="SelectVPNNodeServlet" method="post">
        <table border="1" cellpadding="10">
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
        <br><br>

        <!-- Optional (Automatic Route) -->
        <input type="checkbox" id="autoRoute" name="autoRoute">
        <span>Enable Automatic Routing</span>
        <br><br>

        <!-- Submit button -->
        <input type="submit" value="Submit">
    </form>
    <!-- Network Condition Display -->
    <h2>Network Condition</h2>
    <h3>Node status</h3>
    <div id="networkCondition">
        <%
            String onosResponse = (String) request.getAttribute("onosResponse");
            if (onosResponse != null) {
                try {
                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(onosResponse);
                    JSONArray hosts = jsonResponse.getJSONArray("hosts");

                    if (hosts.length() > 0) {
        %>
        <table border="1" cellpadding="10">
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
                        out.println("<p>No hosts available in ONOS response.</p>");
                    }
                } catch (Exception e) {
                        out.println("<p>Error parsing ONOS response: " + e.getMessage() + "</p>");
                    e.printStackTrace();
                }
            %>
        <%
        } else {
        %>
        <p>No response from ONOS.</p>
        <%
            }
        %>
    </div>

    <!-- Selected VPN Node Information -->
    <h2>Selected VPN Node</h2>
    <div id="selectedVPNNode">
        <!-- Display the VPN node chosen above -->
        <%
            String[] selectedNodes = (String[]) request.getAttribute("selectedNodes");
            Boolean autoRoute = (Boolean) request.getAttribute("autoRoute");
            String autoRouteMessage = (String) request.getAttribute("autoRouteMessage");

            if (selectedNodes != null && selectedNodes.length > 0) {
        %>
            <ul>
                <% for (String node : selectedNodes) { %>
                <li><%= node %></li>
                <% } %>
            </ul>
            <p>Automatic Routing Enabled: <%= autoRoute %></p>

            <% if (autoRouteMessage != null) { %>
                    <p><%= autoRouteMessage %></p>
            <% } %>
        <%
            } else {
        %>
            <p>No service provider selected.</p>
        <%
            }
        %>
    </div>
</body>
</html>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<html>--%>
<%--<head>--%>
<%--    <title>Multi-VPN Home Gateway</title>--%>
<%--    <style>--%>
<%--        body {--%>
<%--            font-family: Arial, sans-serif;--%>
<%--            line-height: 1.6;--%>
<%--            margin: 20px;--%>
<%--        }--%>
<%--        table {--%>
<%--            width: 100%;--%>
<%--            border-collapse: collapse;--%>
<%--            margin: 20px 0;--%>
<%--            font-size: 18px;--%>
<%--            text-align: left;--%>
<%--        }--%>
<%--        th, td {--%>
<%--            padding: 12px;--%>
<%--            border: 1px solid #ddd;--%>
<%--        }--%>
<%--        th {--%>
<%--            background-color: #f4f4f4;--%>
<%--        }--%>
<%--        tr:nth-child(even) {--%>
<%--            background-color: #f9f9f9;--%>
<%--        }--%>
<%--        tr:hover {--%>
<%--            background-color: #f1f1f1;--%>
<%--        }--%>
<%--        .section {--%>
<%--            margin-bottom: 20px;--%>
<%--        }--%>
<%--    </style>--%>
<%--</head>--%>
<%--<body>--%>
<%--<h1>Multi-VPN Home Gateway</h1>--%>

<%--<!-- Node Selection Section -->--%>
<%--<section class="section">--%>
<%--    <h2>Select Service Provider Nodes</h2>--%>
<%--    <form action="SelectVPNNodeServlet" method="post">--%>
<%--        <table>--%>
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
<%--        <br>--%>
<%--        <label>--%>
<%--            <input type="checkbox" name="autoRoute">--%>
<%--            Enable Automatic Routing--%>
<%--        </label>--%>
<%--        <br><br>--%>
<%--        <input type="submit" value="Submit">--%>
<%--    </form>--%>
<%--</section>--%>

<%--<!-- Network Condition Section -->--%>
<%--<section class="section">--%>
<%--    <h2>Network Condition</h2>--%>
<%--    <div id="networkCondition">--%>
<%--        <%--%>
<%--            String onosResponse = (String) request.getAttribute("onosResponse");--%>
<%--            if (onosResponse != null) {--%>
<%--        %>--%>
<%--        <pre><%= onosResponse %></pre>--%>
<%--        <%--%>
<%--        } else {--%>
<%--        %>--%>
<%--        <p>No response from ONOS.</p>--%>
<%--        <%--%>
<%--            }--%>
<%--        %>--%>
<%--    </div>--%>
<%--</section>--%>

<%--<!-- Selected VPN Node Section -->--%>
<%--<section class="section">--%>
<%--    <h2>Selected VPN Node</h2>--%>
<%--    <div id="selectedVPNNode">--%>
<%--        <%--%>
<%--            String[] selectedNodes = (String[]) request.getAttribute("selectedNodes");--%>
<%--            Boolean autoRoute = (Boolean) request.getAttribute("autoRoute");--%>
<%--            String autoRouteMessage = (String) request.getAttribute("autoRouteMessage");--%>

<%--            if (selectedNodes != null && selectedNodes.length > 0) {--%>
<%--        %>--%>
<%--        <ul>--%>
<%--            <% for (String node : selectedNodes) { %>--%>
<%--            <li><%= node %></li>--%>
<%--            <% } %>--%>
<%--        </ul>--%>
<%--        <p>Automatic Routing Enabled: <%= autoRoute != null ? autoRoute : "false" %></p>--%>
<%--        <% if (autoRouteMessage != null) { %>--%>
<%--        <p><%= autoRouteMessage %></p>--%>
<%--        <% } %>--%>
<%--        <%--%>
<%--        } else {--%>
<%--        %>--%>
<%--        <p>No service provider selected.</p>--%>
<%--        <%--%>
<%--            }--%>
<%--        %>--%>
<%--    </div>--%>
<%--</section>--%>
<%--</body>--%>
<%--</html>--%>
