/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Player;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import com.google.gson.*;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author edgar
 */
//@WebServlet("/Player")
public class Player extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Player</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Player at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    protected List<Song> getSongsFromDB() throws SQLException {
        // Database info
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL="jdbc:mysql://localhost:3306/music";
        // For login
        final String USER = "root";
        final String PASS = "gn_28121904";
        Connection conn = null;
        Statement stmt = null;
        List<Song> songs_list = new ArrayList();

        try {
            Class.forName(JDBC_DRIVER);
            
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            
            String query = "SELECT * from songs";
            ResultSet res = stmt.executeQuery(query);
            
            //Gson gson_fin = new Gson();
            //List<Song> songs_list = new ArrayList();
            
            while (res.next()) {
                Song curr = new Song();
                //Gson gson_curr = new Gson();
                
                curr.id = res.getInt("Id");
                curr.album = res.getString("Album");
                curr.name = res.getString("Name");
                curr.perf = res.getString("Perf");
                curr.path = res.getString("Path");
                
                //songs_list.add((String) gson_curr.toJson(curr));
                songs_list.add(curr);
            }
            
            res.close();
            stmt.close();
            conn.close();
        } catch(SQLException se) {
            se.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch(SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch(SQLException se) {
                se.printStackTrace();
            }
        }
        
        return songs_list;
    }
}
