package aed.accesoBDRelaciones_Hoteles.resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrialConnectionsMain {

	public static void main(String[] args) throws SQLException {
		Connection conn = Conectordb.switchConnection(1);
		PreparedStatement hotelExiste = conn.prepareStatement("call proc_insert_habitacion (?, ?, ?, ?, ?, @hotel_exists, @validate_insert)");
		PreparedStatement hotelExiste_param = conn.prepareStatement("select @hotel_exists, @validate_insert;");
		
        hotelExiste.setString(1, "111111");
        hotelExiste.setInt(2, 2);
        hotelExiste.setInt(3, 6);
        hotelExiste.setInt(4, 68);
        hotelExiste.setInt(5, 1);

        hotelExiste.executeQuery();
        ResultSet rs = hotelExiste_param.executeQuery();
        while (rs.next()) {
        	System.out.println(rs.getInt(1)+","+rs.getInt(2));
        }
	}

	private static boolean validarInt(String num) {
		try {
			Integer.parseInt(num);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
