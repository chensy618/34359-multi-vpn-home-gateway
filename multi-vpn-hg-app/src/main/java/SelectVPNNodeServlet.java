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

        String result = connectToOnos();
        out.println("Onos response : " + result);
        // Store data in request attributes
        request.setAttribute("selectedNodes", selectedNodes);
        request.setAttribute("autoRoute", autoRoute);
        // 将ONOS的响应设置为请求属性
        request.setAttribute("onosResponse", result);

        // Forward the request back to index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    private String connectToOnos() {
        try {
            // http://localhost:8181/onos/v1/devices : get all devices
            // http://localhost:8181/onos/v1/hosts : get all hosts
            // http://localhost:8181/onos/v1/links : get all links
            // 设置ONOS API的URL
            URL url = new URL("http://localhost:8181/onos/v1/hosts");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString("onos:rocks".getBytes()));

            // 读取ONOS的响应
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) { // 成功
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                return "ONOS Response: " + content.toString();
            } else {
                return "Failed to connect to ONOS. Response code: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error connecting to ONOS: " + e.getMessage();
        }
    }
}
