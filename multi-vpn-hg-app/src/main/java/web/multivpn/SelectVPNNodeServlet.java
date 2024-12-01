package web.multivpn;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;

import onos.multivpn.*;
import org.json.JSONArray;
import org.json.JSONObject;

import static java.lang.System.*;


@WebServlet(name = "SelectVPNNodeServlet")
public class SelectVPNNodeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean autoRoute = request.getParameter("autoRoute") != null;
        String[] selectedNodes;
        // Determine selected nodes based on autoRoute
        if (autoRoute) {
            // Automatic routing enabled, set default node
            selectedNodes = new String[]{"provider1"}; // Default to first provider
            request.setAttribute("autoRouteMessage", "Automatic routing start");
        } else {
            // Use the single selected node from the radio button
            String selectedNode = request.getParameter("serviceProvider");
            if (selectedNode != null) {
                selectedNodes = new String[]{selectedNode};
            } else {
                selectedNodes = new String[]{}; // Set to an empty array if no node was selected
            }
        }

        OnosAPIClient onosClient = new OnosAPIClient();
        String hostsInfo = onosClient.getHosts();
        out.println("Onos hostsInfo : " + hostsInfo);

        JSONObject hostsJson = new JSONObject(hostsInfo);
        JSONArray hostsArray = hostsJson.getJSONArray("hosts");

        String deviceINfo = onosClient.getDevices();
        out.println("Onos devices : " + deviceINfo);

        // 配置从 h1 -> h2 的流规则
        onosClient.submitFlowRule(
                onosClient.createFlowRule("1", "2", "00:00:00:00:00:01", "00:00:00:00:00:02", "push", "1"),
                "of:0000000000000001", "001" // s1
        );
        onosClient.submitFlowRule(
                onosClient.createFlowRule("1", "2", "00:00:00:00:00:01", "00:00:00:00:00:02", null, "1"),
                "of:0000000000000003", "001" // s3
        );
        onosClient.submitFlowRule(
                onosClient.createFlowRule("1", "2", "00:00:00:00:00:01", "00:00:00:00:00:02", "pop", "1"),
                "of:0000000000000002", "001" // s2
        );

        // 配置从 h2 -> h1 的流规则
        onosClient.submitFlowRule(
                onosClient.createFlowRule("2", "1", "00:00:00:00:00:02", "00:00:00:00:00:01", "push", "1"),
                "of:0000000000000002", "001" // s2
        );
        onosClient.submitFlowRule(
                onosClient.createFlowRule("2", "1", "00:00:00:00:00:02", "00:00:00:00:00:01", null, "1"),
                "of:0000000000000003", "001" // s3
        );
        onosClient.submitFlowRule(
                onosClient.createFlowRule("2", "1", "00:00:00:00:00:02", "00:00:00:00:00:01", "pop", "1"),
                "of:0000000000000001", "001" // s1
        );

        // Respond back to the web ui
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(hostsInfo);

        // Store data in request attributes, 将ONOS的响应设置为请求属性
        request.setAttribute("selectedNodes", selectedNodes);
        request.setAttribute("autoRoute", autoRoute);
        request.setAttribute("onosResponse", hostsInfo);

        // Forward the request back to index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    private void addFlowRule(OnosAPIClient onosClient, String deviceId, String inPort, String outPort,
                             String ethSrc, String ethDst, String vlanOp, String vlanId) {
        try {
            // 创建流规则
            JSONObject flowRule = onosClient.createFlowRule(inPort, outPort, ethSrc, ethDst, vlanOp, vlanId);

            // 提交流规则
            int responseCode = onosClient.submitFlowRule(flowRule, deviceId, "001");

            // 打印日志
            if (responseCode == 200 || responseCode == 201) {
                System.out.println("Flow rule successfully added to device " + deviceId);
            } else {
                System.out.println("Failed to add flow rule to device " + deviceId + ". Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
