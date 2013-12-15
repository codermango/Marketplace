/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package market;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark
 */
public final class JdbcUtility
{

    private static String url = "jdbc:derby://localhost:1527/Market";
    private static String username = "root";
    private static String password = "root";

    private JdbcUtility() //
    {
    }

    static
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(JdbcUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(url, username, password);
    }

    public static void free(ResultSet rs, Statement st, Connection conn)
    {
        try
        {
            if (rs != null)
            {
                rs.close();
            }
        } catch (SQLException ex)
        {
            Logger.getLogger(JdbcUtility.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            try
            {
                if (st != null)
                {
                    st.close();
                }
            } catch (SQLException ex)
            {
                Logger.getLogger(JdbcUtility.class.getName()).log(Level.SEVERE, null, ex);
            } finally
            {
                try
                {
                    if (conn != null)
                    {
                        conn.close();
                    }
                } catch (SQLException ex)
                {
                    Logger.getLogger(JdbcUtility.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
