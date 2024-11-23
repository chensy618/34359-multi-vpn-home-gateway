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
        String result = onosClient.connectToOnos("hosts");
        out.println("Onos response : " + result);

        // Process topology and assign VLANs
        Map<Integer, String[][]> vlanHostMap = onosClient.processTopologyAndAssignVlans(result);

        // Reconfigure existing hosts' VLANs
        onosClient.configureHostsVlan(vlanHostMap);


        // Respond back to the web ui
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(result);

        // Store data in request attributes, 将ONOS的响应设置为请求属性
        request.setAttribute("selectedNodes", selectedNodes);
        request.setAttribute("autoRoute", autoRoute);
        request.setAttribute("onosResponse", result);

        // Forward the request back to index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

}
