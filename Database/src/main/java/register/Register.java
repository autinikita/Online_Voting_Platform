package register;

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

@WebServlet("/Register")
public class Register extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Register() {
        super();
    }

    // doGet method to display data from register table (not changed)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3336/register_db", "root", "Nikita@2004"
            );

            // SQL query to fetch all records from the register table
            PreparedStatement ps = con.prepareStatement("SELECT * FROM register");

            // Start HTML Table
            out.print("<table width=75% border=1>");
            out.print("<caption>Data Store Record:</caption><br/>");

            // Execute the query
            ResultSet rs = ps.executeQuery();
            
            // Table Header
            out.print("<tr>");
            int totalColumn = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= totalColumn; i++) {
                out.print("<th>" + rs.getMetaData().getColumnName(i) + "</th>");
            }
            out.print("</tr>");

            // Table Data
            while (rs.next()) {
                out.print("<tr>");
                for (int i = 1; i <= totalColumn; i++) {
                    out.print("<td>" + rs.getString(i) + "</td>");
                }
                out.print("</tr>");
            }
            out.print("</table>");
        } catch (Exception e) {
            out.print("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }
    }

    // doPost method to handle form submission and check Aadhar and voterid
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Retrieve form data
        String Name = request.getParameter("Name");
        String DOB = request.getParameter("DOB");
        String Address = request.getParameter("Address");
        String Phone = request.getParameter("Phone");
        String Aadhar = request.getParameter("Aadhar");
        String voterid = request.getParameter("voterid");
        String Gmail = request.getParameter("Gmail");
        String password = request.getParameter("password");
        String status = request.getParameter("status");
        

        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the admin database to check Aadhar and voterid
            Connection Admin1 = DriverManager.getConnection(
                "jdbc:mysql://localhost:3336/admin", "root", "Nikita@2004"
            );

            // SQL query to check if Aadhar and voterid exist in the admin database
            String checkQuery = "SELECT * FROM admin WHERE Aadhar = ? AND voterid = ?";
            PreparedStatement checkPs = Admin1.prepareStatement(checkQuery);
            checkPs.setString(1, Aadhar);
            checkPs.setString(2, voterid);

            // Execute the query
            ResultSet checkRs = checkPs.executeQuery();

            // Check if Aadhar and voterid match
            if (!checkRs.next()) {
                // If Aadhar or voterid do not match any record in the admin database
                out.print("<p style='color:red;'>Invalid Aadhar or Voter ID. Please provide valid information.</p>");
            } else {
                // Connect to the register database to insert data into the register table
                Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3336/register_db", "root", "Nikita@2004"
                );

                // SQL query to insert data into the register table
                String query = "INSERT INTO register (Name, DOB, Address, Phone, Aadhar, voterid, Gmail, password,status, voted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(query);

                // Set parameters for the insert query
                ps.setString(1, Name);
                ps.setString(2, DOB);
                ps.setString(3, Address);
                ps.setString(4, Phone);
                ps.setString(5, Aadhar);
                ps.setString(6, voterid);
                ps.setString(7, Gmail);
                ps.setString(8, password);
                ps.setString(9, status);
                ps.setString(10,"N");

                // Execute update
                int s = ps.executeUpdate();
                if (s > 0) {
                    out.print("<p style='color:green;'>Record added successfully.</p>");
                } else {
                    out.print("<p style='color:red;'>Error adding record.</p>");
                }
            }

            // Close the connections
            Admin1.close();
            request.getRequestDispatcher("Registration.html").include(request, response);

        } catch (Exception e) {
            out.print("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }
    }
}