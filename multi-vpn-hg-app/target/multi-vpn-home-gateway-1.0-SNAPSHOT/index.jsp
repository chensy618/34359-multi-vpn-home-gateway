<%@ page import="org.json.JSONObject" %>
<%@ page import="org.json.JSONArray" %><%--
  Created by IntelliJ IDEA.
  User: student
  Date: 11/8/24
  Time: 8:31 PM
  To change this template use File | Settings | File Templates.
--%>
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

        h1 {
            text-align: center;
            margin-top: 20px;
            color: #1a73e8;
        }

        .main-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin: 20px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            max-width: 900px;
        }

        .section {
            width: 100%;
            margin-bottom: 20px;
        }

        h2 {
            color: #1a73e8;
            border-bottom: 2px solid #1a73e8;
            padding-bottom: 5px;
            margin-bottom: 10px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        table th, table td {
            padding: 10px;
            text-align: center;
            border: 1px solid #ddd;
        }

        table th {
            background-color: #1a73e8;
            color: white;
        }

        /* Ensure all buttons are the same size */
        input[type="radio"], input[type="checkbox"], .toggle-button {
            width: 20px;
            height: 20px;
            margin: 0 auto;
            display: inline-block;
        }

        /* Custom toggle button style */
        .toggle-button {
            border: 2px solid #ccc;
            border-radius: 50%;
            background-color: white;
            cursor: pointer;
        }

        .toggle-button.enable {
            background-color: #1a73e8;
            border-color: #1a73e8;
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

        .error {
            color: red;
            font-weight: bold;
        }

        .success {
            color: green;
            font-weight: bold;
        }

        .center {
            text-align: center;
        }
    </style>
    <script>
        // JavaScript to toggle status between enable and disable
        function toggleStatus(button, inputId) {
            const input = document.getElementById(inputId);
            if (button.classList.contains("enable")) {
                button.classList.remove("enable");
                input.value = "disable";
            } else {
                button.classList.add("enable");
                input.value = "enable";
            }
        }
    </script>
</head>
<body>
<h1>Multi-VPN Home Gateway Application</h1>
<div class="main-container">
    <!-- VPN Node Selection -->
    <div class="section">
        <h2>VPN Node Selection</h2>
        <form action="SelectVPNNodeServlet" method="post">
            <table>
                <tr>
                    <th>Nodes</th>
                    <th>Select</th>
                    <th>Enable</th>
                </tr>
                <tr>
                    <td>Service Provider 1 (SP1)</td>
                    <td><input type="radio" name="serviceProvider" value="provider1"></td>
                    <td>
                        <div class="toggle-button" onclick="toggleStatus(this, 'statusProvider1')"></div>
                        <input type="hidden" id="statusProvider1" name="statusProvider1" value="disable">
                    </td>
                </tr>
                <tr>
                    <td>Service Provider 2 (SP2)</td>
                    <td><input type="radio" name="serviceProvider" value="provider2"></td>
                    <td>
                        <div class="toggle-button" onclick="toggleStatus(this, 'statusProvider2')"></div>
                        <input type="hidden" id="statusProvider2" name="statusProvider2" value="disable">
                    </td>
                </tr>
                <tr>
                    <td>Service Provider 3 (SP3)</td>
                    <td><input type="radio" name="serviceProvider" value="provider3"></td>
                    <td>
                        <div class="toggle-button" onclick="toggleStatus(this, 'statusProvider3')"></div>
                        <input type="hidden" id="statusProvider3" name="statusProvider3" value="disable">
                    </td>
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

    <!-- Network Topology -->
    <div class="section">
        <h2>Network Topology</h2>
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

    <!-- Selected VPN Node -->
    <div class="section">
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
</div>
</body>
</html>





