package web.multivpn;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import onos.multivpn.OnosAPIClient;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "SelectVPNNodeServlet")
public class SelectVPNNodeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean autoRoute = request.getParameter("autoRoute") != null;
        String selectedNode;
        // Determine selected nodes based on autoRoute
        if (autoRoute) {
            // Automatic routing enabled, set default node
            selectedNode = new String("provider1"); // Default to first provider
            request.setAttribute("autoRouteMessage", "Automatic routing start");
        } else {
            // Use the single selected node from the radio button
            selectedNode = request.getParameter("serviceProvider");
            if (selectedNode != null) {
                request.setAttribute("selectedNode", selectedNode);
            } else {
                selectedNode = "";
                request.setAttribute("selectedNode", selectedNode);// Set to an empty array if no node was selected
            }
        }

        OnosAPIClient onosClient = new OnosAPIClient();
        String hostsInfo = onosClient.getHosts();
        JSONObject hostsJson = new JSONObject(hostsInfo);
        JSONArray hostsArray = hostsJson.getJSONArray("hosts");

        // 定义 VLAN 映射
        Map<String, String> switchToVlanMap = new HashMap<>();
        switchToVlanMap.put("of:0000000000000001", "10");
        switchToVlanMap.put("of:0000000000000002", "20");
        switchToVlanMap.put("of:0000000000000003", "30");
        switchToVlanMap.put("of:0000000000000004", "40");

        // 遍历主机信息并分配 VLAN
        for (int i = 0; i < hostsArray.length(); i++) {
            JSONObject host = hostsArray.getJSONObject(i);
            String mac = host.getString("mac");
            String vlan = host.getString("vlan");
            JSONArray ipAddresses = host.getJSONArray("ipAddresses");
            String ip = ipAddresses.length() > 0 ? ipAddresses.getString(0) : null;
            JSONArray locations = host.getJSONArray("locations");
            JSONObject location = locations.getJSONObject(0);
            String switchId = location.getString("elementId");
            String port = location.getString("port");

            // 跳过已有 VLAN 的主机
//            if (!"None".equals(vlan)) {
//                System.out.println("Host with MAC: " + mac + " already has VLAN: " + vlan + ". Skipping.");
//                continue;
//            }

            // 根据交换机分配 VLAN
            if (switchToVlanMap.containsKey(switchId)) {
                String vlanId = switchToVlanMap.get(switchId);
                onosClient.pushVlan(mac, vlanId, ip, switchId, port);
            }
        }

        // 设置响应数据
        request.setAttribute("debugInfoMessage", hostsInfo);
//        request.setAttribute("selectedNode", selectedNode);
        request.setAttribute("autoRoute", autoRoute);
        request.setAttribute("onosResponse", hostsInfo);

        // 转发请求到 index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }
}

