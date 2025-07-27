package result;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/Result")
public class Result extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Result() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3336/register_db", "root", "Nikita@2004");

            // Fetch candidate names and their total votes sorted in descending order
            String sql = "SELECT candidate_name, COUNT(*) as total_votes FROM voteing GROUP BY candidate_name ORDER BY total_votes DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // Start HTML Table
            out.print("<h2>Voting Results</h2>");
            out.print("<table border='1' cellspacing='0' cellpadding='10'>");
            out.print("<tr><th>Candidate Name</th><th>Total Votes</th></tr>");

            // Table Data
            while (rs.next()) {
                out.print("<tr>");
                out.print("<td>" + rs.getString("candidate_name") + "</td>");
                out.print("<td>" + rs.getInt("total_votes") + "</td>");
                out.print("</tr>");
            }
            out.print("</table>");

            // Auto-refresh the page every 5 seconds
            out.print("<script>setTimeout(function(){ location.reload(); }, 5000);</script>");

            // Close resources
            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            out.print("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }
    }
}
