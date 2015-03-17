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
            List<Song> selected_songs;
            selected_songs = getSongsFromDB();
            Gson gson = new Gson();
            String songs_json = gson.toJson(selected_songs);
            out.println(songs_json);
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

    protected List<Song> getSongsFromDB() {
        // Database info
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL="jdbc:mysql://localhost:3306/music";
        // For login
        final String USER = "root";
        final String PASSW = "28121904";
        Connection conn = null;
        Statement stmt = null;
        List<Song> songs_list = new ArrayList();
      
        try {
            Class.forName(JDBC_DRIVER).newInstance();
            String query = "SELECT * FROM songs";
            conn = DriverManager.getConnection(DB_URL, USER, PASSW);
            stmt = conn.prepareStatement(query);
            
            ResultSet res = stmt.executeQuery(query);
            
            while (res.next()) {
                Song curr = new Song();
                curr.id = res.getInt("Id");
                curr.album = res.getString("Album");
                curr.name = res.getString("Name");
                curr.perf = res.getString("Perf");
                curr.path = res.getString("Path");
                
                //log("id:"+curr.id);
                //log("name:"+curr.name);
                
                songs_list.add(curr);
            }
            
            res.close();
            stmt.close();
            conn.close();
        } catch(SQLException se) {
            log(se.getMessage());
        } catch(Exception e) {
            log(e.getMessage());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch(SQLException se2) {
                log(se2.getMessage());
            }
            try {
                if (conn != null)
                    conn.close();
            } catch(SQLException se) {
                log(se.getMessage());
            }
        }
        
        return songs_list;
    }
}
