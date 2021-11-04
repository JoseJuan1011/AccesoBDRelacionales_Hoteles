package aed.accesoBDRelaciones_Hoteles.resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrialConnectionsMain {

	public static void main(String[] args) throws SQLException {
		Connection conn = Conectordb.switchConnection(2);
		PreparedStatement ps = conn.prepareStatement("exec procHabitacionesHotel @nomHotel = ?;");
		ps.setEscapeProcessing(true);
        ps.setQueryTimeout(0);
		ps.setString(1, "Barceló Canarias");
        ResultSet rs = ps.executeQuery();
        System.out.println("numHabitacion | capacidad | preciodia | activa");
        System.out.println("----------------------------------------------");
        while (rs.next()) {
        	System.out.println("      "+rs.getString(1).trim()+"       |     "+rs.getInt(2)+"     |    "+rs.getInt(3)+"     |    "+rs.getInt(4));
        }
        System.out.println("----------------------------------------------");
	}
}