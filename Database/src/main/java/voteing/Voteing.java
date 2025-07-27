package voteing;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/Voteing")
public class Voteing extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve voterid from session
        HttpSession session = request.getSession();
        String voterid = (String) session.getAttribute("voterid");

        if (voterid == null) {
            response.getWriter().println("Session expired. Please log in again.");
            return;
        }

        String candidate = request.getParameter("candidate");
        if (candidate == null || candidate.isEmpty()) {
            response.getWriter().println("Invalid vote. Please try again.");
            return;
        }

        Connection conn = null;
        PreparedStatement checkVotedStmt = null;
        PreparedStatement updateVoteStmt = null;
        PreparedStatement insertVoteStmt = null;
        ResultSet rs = null;

        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3336/register_db", "root", "Nikita@2004");

            // Check if voter has already voted
            String checkVoteSql = "SELECT voted FROM register WHERE voterid = ?";
            checkVotedStmt = conn.prepareStatement(checkVoteSql);
            checkVotedStmt.setString(1, voterid);
            rs = checkVotedStmt.executeQuery();

            if (rs.next()) {
                String votedStatus = rs.getString("voted");
                rs.close();
                checkVotedStmt.close();

                if ("N".equalsIgnoreCase(votedStatus)) {
                    // Update voted status
                    String updateVoteStatusSql = "UPDATE register SET voted = 'Y' WHERE voterid = ?";
                    updateVoteStmt = conn.prepareStatement(updateVoteStatusSql);
                    updateVoteStmt.setString(1, voterid);
                    updateVoteStmt.executeUpdate();
                    updateVoteStmt.close();

                    // Insert vote into voting table
                    String insertVoteSql = "INSERT INTO voteing (candidate_name) VALUES (?)";
                    insertVoteStmt = conn.prepareStatement(insertVoteSql);
                    insertVoteStmt.setString(1, candidate);
                    insertVoteStmt.executeUpdate();
                    insertVoteStmt.close();

                    // ✅ Clear session after successful vote
                    session.invalidate();

                    conn.close();
                    response.sendRedirect("success.html");
                } else {
                    conn.close();
                    //response.getWriter().println("You Have Already Voted!");
                    response.sendRedirect("ALVOTE.html");
                }
            } else {
                conn.close();
                response.getWriter().println("No record found for this voter.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error occurred while voting: " + e.getMessage());
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load PostgreSQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3336/admin", "root", "Nikita@2004");

            // SQL query to fetch all records from the voteing table
            PreparedStatement ps = con.prepareStatement("SELECT * FROM voteing");

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
            e.printStackTrace(); // Log the error details for debugging
        }
    }
}
