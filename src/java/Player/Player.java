/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import com.google.gson.*;
import java.io.OutputStream;
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
	protected void processSongListRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/json;charset=UTF-8");
		try (PrintWriter out = response.getWriter()) {
			List<Song> selected_songs;
			String param;
			int begin = 0, amount = 10;
			if ((param = request.getParameter("begin")) != null) {
				try {
					begin = Integer.parseInt(param, 10);
				}
				catch (NumberFormatException nfe) {
					out.println("Illegal value of begin: " + begin);
					log(nfe.toString());
				}
			}
			if ((param = request.getParameter("amount")) != null) {
				try {
					amount = Integer.parseInt(param, 10);
				}
				catch (NumberFormatException nfe) {
					out.println("Illegal value of amount: " + amount);
					log(nfe.toString());
				}
			}
			selected_songs = getSongsFromDB(begin, amount);
			Gson gson = new Gson();
			String songs_json = gson.toJson(selected_songs);
			out.println(songs_json);
		}
	}
	
	protected void processFileRequest(int id, HttpServletResponse response)
			throws ServletException, IOException {
		String path = getSongPath(id);
		if (path == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Song not found");
			return;
		}
		String filePath = getServletContext().getRealPath("/"+path);
		log("File path: " + filePath);
		File downloadFile = new File(filePath);
		if (!downloadFile.canRead()) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error reading file on server");
			return;
		}
		
		ServletContext context = getServletContext();
		
		String mimeType = context.getMimeType(filePath);
		if (mimeType == null) {
			mimeType = "application/octet-stream";
		}
		
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());
		
		String headerValue = String.format("inline; filename=\"%s\"", 
				downloadFile.getName());
		response.setHeader("Content-Disposition", headerValue);
		
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		
		try (OutputStream outStream = response.getOutputStream()) {
			try (FileInputStream inStream = new FileInputStream(downloadFile)) {
				while ((bytesRead = inStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
			}
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
		if (request.getParameter("id") != null) {
			String id = request.getParameter("id");
			try {
				int id_int = Integer.parseInt(id, 10);
				processFileRequest(id_int, response);
			}
			catch (NumberFormatException nfe) {
				PrintWriter out = response.getWriter();
				out.println("Illegal value of id: " + id);
				log(nfe.toString());
			}
		} else {
			processSongListRequest(request, response);
		}
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
		processSongListRequest(request, response);
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
	
	protected Connection connectToDB() {
		// Database info
		final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		final String DB_URL="jdbc:mysql://localhost:3306/music";
		// For login
		final String USER = "root";
		final String PASSW = "gn_28121904";
		
		Connection conn = null;
		
		try {
			Class.forName(JDBC_DRIVER).newInstance();
			conn = DriverManager.getConnection(DB_URL, USER, PASSW);
		} catch(SQLException se) {
			log(se.getMessage());
		} catch(Exception ex) {
			log(ex.getMessage());
		}
		
		return conn;
	}

	protected List<Song> getSongsFromDB(int begin, int amount) {
		Statement stmt = null;
		List<Song> songs_list = new ArrayList();
		String query = "SELECT Id, Album, Name, Perf FROM songs LIMIT "
			+ begin + "," + amount;
		
		Connection conn = connectToDB();
		try {
			stmt = conn.prepareStatement(query);
			try (ResultSet res = stmt.executeQuery(query)) {
				while (res.next()) {
					Song curr = new Song();
					curr.id = res.getInt("Id");
					curr.album = res.getString("Album");
					curr.name = res.getString("Name");
					curr.perf = res.getString("Perf");
				
					songs_list.add(curr);
				}
			}
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
	
	protected String getSongPath(int id) {
		Connection conn = connectToDB();
		Statement stmt = null;
		String query = "SELECT Path FROM songs WHERE Id="+id;
		String path = null;
		
		try {
			stmt = conn.prepareStatement(query);
			try (ResultSet res = stmt.executeQuery(query)) {
				if (res.next())
					path = res.getString("Path");
			}
		} catch(SQLException se) {
			log(se.getMessage());
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
		
		return path;
	}
}
