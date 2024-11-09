<%--
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
    <div id="networkCondition">
        <!-- Show network condition -->
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
