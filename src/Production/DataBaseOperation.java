/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Production;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Wei Wang
 */
public class DataBaseOperation{
    private String dbName , userName , pw;
    private Statement stmt;
    DataBaseOperation(String dbName , String userName , String pw) throws SQLException{
        this.dbName = dbName;
        this.userName = userName;
        this.pw = pw;
        
        Connection mycon = DriverManager.getConnection(dbName, userName, pw);
        stmt = mycon.createStatement();
    }
    
    public ResultSet read(String sql) throws SQLException{
        return stmt.executeQuery(sql);
    }
    
    public void write(String sql) throws SQLException{
        stmt.executeUpdate(sql);
    }
    public void close() throws SQLException{
        stmt.getConnection().close();
    }
}
