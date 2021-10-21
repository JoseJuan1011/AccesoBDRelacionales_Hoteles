package aed.conectorBD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConectorBD_MySQL_TransactSQL {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		Connection conn;

		conn = switchConnection(2);

		PreparedStatement p_stmt = conn.prepareStatement("SELECT codHotel, numHabitacion FROM HABITACIONES");
		p_stmt.execute("use bdHoteles;");
		System.out.println("bdHoteles Seleccionada");

		ResultSet rs = p_stmt.executeQuery();

		while (rs.next()) {
			System.out
					.println("codHotel: " + rs.getString("codHotel") + " numHabitacion: " + rs.getInt("numHabitacion"));
		}

	}

	private static Connection switchConnection(int dbNumber) {
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
			return null;

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

		return conn;
	}

	private static Connection TransactSQLConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		String serverUrl = "jdbc:sqlserver://localhost:1433;databaseName=bdHoteles";
		String username = "root";
		String password = "";

		Connection conn = DriverManager.getConnection(serverUrl, username, password);
		System.out.println("Connected Succesfully (SQL Server)");

		return conn;
	}
}
