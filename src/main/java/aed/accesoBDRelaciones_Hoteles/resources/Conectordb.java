package aed.accesoBDRelaciones_Hoteles.resources;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conectordb {
	
	public static Connection switchConnection(int dbNumber) {
		switch (dbNumber) {
		case 1:
			try {
				return MySQLConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}

		case 2:
			try {
				return TransactSQLConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}

		case 3:
			try {
				return AccessSQLConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}

		default:
			System.out.println("No ha seleccionado base de datos");
			return null;
		}
	}

	private static Connection MySQLConnection() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");

		String serverUrl = "jdbc:mysql://localhost:3306/";
		String username = "root";
		String password = "";

		Connection conn = DriverManager.getConnection(serverUrl, username, password);
		System.out.println("Connected Succesfully (MySQL)");
		
		conn.prepareStatement("use bdHoteles;").executeUpdate();

		return conn;
	}

	private static Connection TransactSQLConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		String serverUrl = "jdbc:sqlserver://localhost:1433;databaseName=bdHoteles";
		String username = "root";
		String password = "root";

		Connection conn = DriverManager.getConnection(serverUrl, username, password);
		System.out.println("Connected Succesfully (SQL Server)");
		
		conn.prepareStatement("use bdHoteles").executeUpdate();

		return conn;
	}
	
	private static Connection AccessSQLConnection() throws ClassNotFoundException, SQLException {
		Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		
		String dbURL = "jdbc:ucanaccess://" + new File("src\\main\\resources\\bdHoteles.accdb").getAbsolutePath();
		
		Connection conn = DriverManager.getConnection(dbURL);
		System.out.println("Connected Succesfully (Access)");
		
		return conn;
	}
}
