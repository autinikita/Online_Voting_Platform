package login;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/Login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Login() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // For GET requests, just display the login page (optional, typically you'd handle POST for form submissions)
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("Login.html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Retrieve form data (email and password)
        String Gmail = request.getParameter("Gmail");
        String password = request.getParameter("password");

        // If either field is empty, show an error
        if (Gmail == null || Gmail.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            out.print("<p style='color:red;'>Please fill in both Gmail and password fields.</p>");
            request.getRequestDispatcher("Login.html").include(request, response);
            return;
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3336/register_db", "root", "Nikita@2004");

            // SQL query to check if the user exists with the given email and password
            String query = "SELECT * FROM register WHERE Gmail = ? AND password = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, Gmail);
            ps.setString(2, password);

            // Execute the query
            rs = ps.executeQuery();

            // If a matching record is found, login successful
            if (rs.next()) {
               String voterid=rs.getString("voterid");
               HttpSession session=request.getSession();
               session.setAttribute("voterid",voterid);
                response.sendRedirect("vote.html");
            } else {
                // Invalid credentials
                out.print("<p style='color:red;'>Invalid Gmail or password. Please try again.</p>");
                request.getRequestDispatcher("Login.html").include(request, response);
            }
        } catch (Exception e) {
            out.print("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}