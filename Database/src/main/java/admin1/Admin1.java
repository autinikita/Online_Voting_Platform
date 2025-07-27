package admin1;

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

@WebServlet("/Admin1")
public class Admin1 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Admin1() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load PostgreSQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            out.print("Driver Loaded<br>");

            // Establish the connection
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3336/admin", "root", "Nikita@2004");
            out.print("Connection Established<br>");

            // Execute SELECT query to fetch all records from the "admin" table
            PreparedStatement ps = con.prepareStatement("SELECT * FROM admin");
            ResultSet rs = ps.executeQuery();

            // Display records in an HTML table
            out.print("<table width=75% border=1>");
            out.print("<caption>Data Store Record:</caption><br/>");

            // Table Header
            out.print("<tr><th>Aadhar</th><th>Voter ID</th></tr>");
            while (rs.next()) {
                out.print("<tr>");
                out.print("<td>" + rs.getString("Aadhar") + "</td>");
                out.print("<td>" + rs.getString("voterid") + "</td>");
                out.print("</tr>");
            }
            out.print("</table>");

            // Close the resources
            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            out.print("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        } finally {
            out.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String Aadhar = request.getParameter("Aadhar");
        String voterid = request.getParameter("voterid");

        // Validate Aadhar and Voter ID inputs
        if (Aadhar == null || Aadhar.isEmpty() || voterid == null || voterid.isEmpty()) {
            out.print("<p style='color:red;'>Error: Aadhar and Voter ID are required.</p>");
            return;
        }

        try {
            // Load PostgreSQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3336/admin", "root", "Nikita@2004");

            // Check if Aadhar already exists in the database (since it's likely a unique field)
            String checkAadharQuery = "SELECT COUNT(*) FROM admin WHERE Aadhar = ?";
            PreparedStatement checkAadharPs = con.prepareStatement(checkAadharQuery);
            checkAadharPs.setString(1, Aadhar);

            ResultSet rsAadhar = checkAadharPs.executeQuery();
            rsAadhar.next();
            int aadharCount = rsAadhar.getInt(1); // Get the count of records with the same Aadhar

            if (aadharCount > 0) {
                // If Aadhar already exists, return a custom error message
                out.print("<p style='color:red;'>Error: The Aadhar number " + Aadhar + " already exists.</p>");
            } else {
                // Check if Voter ID already exists in the database (if required to be unique)
                String checkVoterIdQuery = "SELECT COUNT(*) FROM admin WHERE voterid = ?";
                PreparedStatement checkVoterIdPs = con.prepareStatement(checkVoterIdQuery);
                checkVoterIdPs.setString(1, voterid);

                ResultSet rsVoterId = checkVoterIdPs.executeQuery();
                rsVoterId.next();
                int voterIdCount = rsVoterId.getInt(1); // Get the count of records with the same voterid

                if (voterIdCount > 0) {
                    // If Voter ID already exists, return a custom error message
                    out.print("<p style='color:red;'>Error: The Voter ID " + voterid + " already exists.</p>");
                } else {
                    // If no duplicates found, proceed with insertion
                    String query = "INSERT INTO admin (Aadhar, voterid) VALUES (?, ?)";
                    PreparedStatement ps = con.prepareStatement(query);

                    ps.setString(1, Aadhar);
                    ps.setString(2, voterid);

                    int status = ps.executeUpdate();
                    if (status > 0) {
                        out.print("<p style='color:green;'>Record added successfully.</p>");
                    } else {
                        out.print("<p style='color:red;'>Error adding record.</p>");
                    }

                    ps.close();
                }

                rsVoterId.close();
                checkVoterIdPs.close();
            }

            rsAadhar.close();
            checkAadharPs.close();
            con.close();
        } catch (Exception e) {
            out.print("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        } finally {
            out.close();
        }
    }
}