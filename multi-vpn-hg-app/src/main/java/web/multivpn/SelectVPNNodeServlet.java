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
        String vlan = "10"; // default VLAN ID

        // Determine automatic routing or manually selected nodes
        if (autoRoute) {
            selectedNodes = new String[]{"provider1"}; // Automatic routing selects provider1 by default
            request.setAttribute("autoRouteMessage", "Automatic routing enabled");
        } else {
            String selectedNode = request.getParameter("serviceProvider");
            if (selectedNode != null) {
                selectedNodes = new String[]{selectedNode};
            } else {
                selectedNodes = new String[]{}; // No nodes selected
            }
        }

        // Calling ONOS API Client
        OnosAPIClient onosClient = new OnosAPIClient();

        // Clean up old flow rules
        cleanOldFlowRules(onosClient);

        // Traverse the selected nodes and dynamically configure flow rules
        for (String selectedNode : selectedNodes) {
            switch (selectedNode) {
                case "provider1":
                    vlan = "10";
                    configureFlowRules(onosClient, "h1", "h2", "s1", "s2", vlan);
                    break;
                case "provider2":
                    vlan = "20";
                    configureFlowRules(onosClient, "h1", "h3", "s1", "s3", vlan);
                    break;
                case "provider3":
                    vlan = "30";
                    configureFlowRules(onosClient, "h1", "h4", "s1", "s4", vlan);
                    break;
                default:
                    System.out.println("Invalid serviceProvider selected: " + selectedNode);
            }
        }

        // Return results to the web interface
        String hostsInfo = onosClient.getHosts();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(hostsInfo);

        // Storing data in request attributes
        request.setAttribute("selectedNodes", selectedNodes);
        request.setAttribute("autoRoute", autoRoute);
        request.setAttribute("onosResponse", hostsInfo);

        // Forward back to index.jsp page
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    private void configureFlowRules(OnosAPIClient onosClient, String srcHost, String dstHost,
                                    String srcSwitch, String dstSwitch, String vlan) {
        // get MAC address and switch ID
        String srcMac = getMacAddress(srcHost);
        String dstMac = getMacAddress(dstHost);
        String srcDeviceId = getDeviceID(srcSwitch);
        String dstDeviceId = getDeviceID(dstSwitch);

        // config the inport and outport
        String srcInPort = "1";  // srcHost -> srcSwitch 的入口端口
        String srcOutPort;       // srcSwitch -> dstSwitch 的出口端口
        String dstInPort = "1";  // dstSwitch -> srcSwitch 的入口端口
        String dstOutPort = "2"; // dstSwitch -> dstHost 的出口端口

        // config the outport accordind to the vlan ID
        switch (vlan) {
            case "10": // VLAN 10: h1 -> h2
                srcOutPort = "2"; // srcSwitch -> dstSwitch port
                dstOutPort = "2"; // dstSwitch -> dstHost port
                break;
            case "20": // VLAN 20: h1 -> h3
                srcOutPort = "3"; // srcSwitch -> dstSwitch port
                dstOutPort = "2"; // dstSwitch -> dstHost port
                break;
            case "30": // VLAN 30: h1 -> h4
                srcOutPort = "4"; // srcSwitch -> dstSwitch port
                dstOutPort = "2"; // dstSwitch -> dstHost port
                break;
            default:
                System.out.println("Invalid VLAN ID: " + vlan);
                return;
        }

        // print flow configuration
        out.println("Configuring flow rules:");
        out.println("VLAN ID: " + vlan);
        out.println("Source Host: " + srcHost + " (MAC: " + srcMac + ")");
        out.println("Destination Host: " + dstHost + " (MAC: " + dstMac + ")");
        out.println("Source Switch: " + srcSwitch + " (Device ID: " + srcDeviceId + ")");
        out.println("Destination Switch: " + dstSwitch + " (Device ID: " + dstDeviceId + ")");
        out.println("Source Switch Ports: IN=" + srcInPort + ", OUT=" + srcOutPort);
        out.println("Destination Switch Ports: IN=" + dstInPort + ", OUT=" + dstOutPort);

        // config h1 -> h2/h3/h4 flow rules : forward direction
        onosClient.submitFlowRule(
                onosClient.createFlowRule(srcInPort, srcOutPort, srcMac, dstMac, "push", vlan),
                srcDeviceId, "001"
        );
        onosClient.submitFlowRule(
                onosClient.createFlowRule(dstInPort, dstOutPort, srcMac, dstMac, "pop", vlan),
                dstDeviceId, "001"
        );

        // config h2/h3/h4 -> h1 flow rules : backward direction
        onosClient.submitFlowRule(
                onosClient.createFlowRule(dstOutPort, dstInPort, dstMac, srcMac, "push", vlan),
                dstDeviceId, "001"
        );
        onosClient.submitFlowRule(
                onosClient.createFlowRule(srcOutPort, srcInPort, dstMac, srcMac, "pop", vlan),
                srcDeviceId, "001"
        );

        System.out.println("Flow rules configured successfully for VLAN " + vlan);
    }

//    // 配置流规则的方法: test one route to make sure the flow rules work
//    private void configureFlowRules(OnosAPIClient onosClient, String srcHost, String dstHost,
//                                    String srcSwitch, String dstSwitch, String vlan) {
//        // 获取 MAC 地址和设备 ID
//        String srcMac = getMacAddress(srcHost);
//        String dstMac = getMacAddress(dstHost);
//        String srcDeviceId = getDeviceID(srcSwitch);
//        String dstDeviceId = getDeviceID(dstSwitch);
//
//        // 设置交换机的输入和输出端口
//        String srcInPort = "1"; // srcHost -> srcSwitch 端口
//        String srcOutPort = "2"; // srcSwitch -> dstSwitch 端口
//        String dstInPort = "1"; // dstSwitch -> srcSwitch 端口
//        String dstOutPort = "2"; // dstSwitch -> dstHost 端口
//
//        // 配置 h1 -> h2 的流规则 (正向流)
//        onosClient.submitFlowRule(
//                onosClient.createFlowRule(srcInPort, srcOutPort, srcMac, dstMac, "push", vlan),
//                srcDeviceId, "001"
//        );
//        onosClient.submitFlowRule(
//                onosClient.createFlowRule(dstInPort, dstOutPort, srcMac, dstMac, "pop", vlan),
//                dstDeviceId, "001"
//        );
//
//        // 配置 h2 -> h1 的流规则 (反向流)
//        onosClient.submitFlowRule(
//                onosClient.createFlowRule(dstOutPort, dstInPort, dstMac, srcMac, "push", vlan),
//                dstDeviceId, "001"
//        );
//        onosClient.submitFlowRule(
//                onosClient.createFlowRule(srcOutPort, srcInPort, dstMac, srcMac, "pop", vlan),
//                srcDeviceId, "001"
//        );
//    }

    // clear old flow rules, this is also important, because every time we need to make sure src could only communicate with dst.
    private void cleanOldFlowRules(OnosAPIClient onosClient) {
        onosClient.clearAllFlows("001");
        System.out.println("Old flow rules cleared.");
    }

    // get mac address according to host name
    private String getMacAddress(String host) {
        Map<String, String> macAddresses = new HashMap<>();
        macAddresses.put("h1", "00:00:00:00:00:01");
        macAddresses.put("h2", "00:00:00:00:00:02");
        macAddresses.put("h3", "00:00:00:00:00:03");
        macAddresses.put("h4", "00:00:00:00:00:04");
        return macAddresses.getOrDefault(host, "");
    }

    // get switch id according to the switch name
    private String getDeviceID(String switchName) {
        Map<String, String> deviceMap = new HashMap<>();
        deviceMap.put("s1", "of:0000000000000001");
        deviceMap.put("s2", "of:0000000000000002");
        deviceMap.put("s3", "of:0000000000000003");
        deviceMap.put("s4", "of:0000000000000004");
        return deviceMap.getOrDefault(switchName, null);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }
}



