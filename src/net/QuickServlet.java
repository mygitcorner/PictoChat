package net;
 
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

//@WebServlet(name="QuickServlet",urlPatterns={"/QuickServlet"})

public class QuickServlet extends HttpServlet {
 
    /**
     * this life-cycle method is invoked when this servlet is first accessed
     * by the client
     */
    public void init(ServletConfig config) {
        System.out.println("Servlet is being initialized");
    }
 
    /**
     * handles HTTP GET request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.println("<html>Hello, I am a Java servlet!</html>");
        writer.flush();
    }
 
    /**
     * handles HTTP POST request
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String paramWidth = request.getParameter("width");
        int width = Integer.parseInt(paramWidth);
 
        String paramHeight = request.getParameter("height");
        int height = Integer.parseInt(paramHeight);
 
        long area = width * height;
 
        PrintWriter writer = response.getWriter();
        writer.println("<html>Area of the rectangle is: " + area + "</html>");
        writer.flush();
 
    }
 
    /**
     * this life-cycle method is invoked when the application or the server
     * is shutting down
     */
    public void destroy() {
        System.out.println("Servlet is being destroyed");
    }
}