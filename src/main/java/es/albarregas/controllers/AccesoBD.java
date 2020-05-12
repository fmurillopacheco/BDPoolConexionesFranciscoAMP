/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.albarregas.controllers;

import es.albarregas.beans.Ave;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.spi.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author Francisco_Antonio
 */
@WebServlet(name = "AccesoBD", urlPatterns = {"/AccesoBD"})
public class AccesoBD extends HttpServlet {

DataSource dataSource;
public void init(ServletConfig config) throws ServletException{
    try{
        Context contextoIncial= new InitialContext();
        dataSource=(DataSource) contextoInicial.lookup()
        
    }
}
    
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Connection conexion = null;
        Statement sentencia = null;
        PreparedStatement preparada = null;
        ResultSet resultado = null;

        Ave ave = null;
        ArrayList<Ave> aves = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("No existe el driver");
            ex.printStackTrace();
        }

        String cadenaConexion = "jdbc:mysql://localhost:3306/pruebasjava";
        String anilla = request.getParameter("una");
        String url = null;
        String sql = null;

        try {
            conexion = DriverManager.getConnection(cadenaConexion, "java2019", "2019");

            if (request.getParameter("anilla") != null) {
                if (anilla != null && anilla != "") {

                    try {
                        if (Integer.parseInt(anilla) > 0) {
                            sql = "select * from aves where anilla=?";
                            preparada = conexion.prepareStatement(sql);
                            preparada.setString(1, anilla);

                            resultado = preparada.executeQuery();
                            if (resultado.next()) {
                                ave = new Ave();
                                ave.setAnilla(resultado.getString(1));
                                ave.setEspecie(resultado.getString(2));
                                ave.setLugar(resultado.getString(3));
                                ave.setFecha(resultado.getString(4));
                                request.setAttribute("una", ave);
                                url = "unRegistro.jsp";
                                resultado.close();
                                preparada.close();
                                conexion.close();
                            } else {
                                url = "error.jsp";
                                request.setAttribute("error", "La anilla no existe");
                            }

                        } else {
                            url = "error.jsp";
                            request.setAttribute("error", "Introduce un nº válido");
                        }
                    } catch (NumberFormatException e) {
                        url="error.jsp";
                        request.setAttribute("error", "Introduce nº");
                    }

                } else {
                    url = "error.jsp";
                    request.setAttribute("error", "Introduce nº");
                }
            }

            if (request.getParameter("todas") != null) {
                sql = "select * from aves";
                try {
                    preparada = conexion.prepareStatement(sql);
                } catch (SQLException ex) {
                    Logger.getLogger(AccesoBD.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    resultado = preparada.executeQuery();
                    aves = new ArrayList<Ave>();
                    while (resultado.next()) {
                        ave = new Ave();
                        ave.setAnilla(resultado.getString(1));
                        ave.setEspecie(resultado.getString(2));
                        ave.setLugar(resultado.getString(3));
                        ave.setFecha(resultado.getString(4));
                        aves.add(ave);
                    }
                    request.setAttribute("aves", aves);
                    url = "varios.jsp";
                } catch (SQLException e) {
                    System.out.println("Error");
                    System.out.println(e);
                }
            }
            if (request.getParameter("algunas") != null && !request.getParameter("algunas").equals("")) {

                try {
                    int numero = Integer.parseInt(anilla);
                    if (numero > 0) {
                        try {
                            sql = "select * from aves order by rand() limit " + numero;
                            sentencia = conexion.createStatement();
                            resultado = sentencia.executeQuery(sql);
                            aves = new ArrayList<Ave>();
                            while (resultado.next()) {
                                ave = new Ave();
                                ave.setAnilla(resultado.getString(1));
                                ave.setEspecie(resultado.getString(2));
                                ave.setLugar(resultado.getString(3));
                                ave.setFecha(resultado.getString(4));
                                aves.add(ave);
                            }
                            request.setAttribute("aves", aves);
                            url = "varios.jsp";
                        } catch (SQLException e) {
                            request.setAttribute("error", "Error al acceder a la tabla");
                            url = "error.jsp";
                        }
                    } else {
                        url = "error.jsp";
                        request.setAttribute("error", "El nº tiene que se mayor que 0");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Error");
                    System.out.println(e);
                    request.setAttribute("error", "Tienes que introducir un nº");
                    url = "error.jsp";
                }
            }

        } catch (SQLException ex) {
            System.out.println("El código de error es " + ex.getErrorCode());
            ex.printStackTrace();
        }

        request.getRequestDispatcher(url).forward(request, response);

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

}
