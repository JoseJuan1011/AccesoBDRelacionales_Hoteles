package aed.accesoBDRelaciones_Hoteles.resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class TrialConnectionsMain {

	public static void main(String[] args) throws SQLException {
		Scanner teclado = new Scanner(System.in);
		
		int tipoDB = 3;
		Connection conn = Conectordb.switchConnection(tipoDB);
		
		PreparedStatement ps = conn.prepareStatement(""
				+ "select habitaciones.codHotel, nomHotel, numHabitacion, capacidad, preciodia, activa from habitaciones "
				+ "inner join hoteles on hoteles.codHotel=habitaciones.codHotel");
		
		if (tipoDB==1)
			ps.execute("use bdHoteles");
		
		ResultSet rs = ps.executeQuery();
		
		System.out.println("codHotel |       nomHotel      | numHabitacion | capacidad | preciodia | activa");
		System.out.println("---------------------------------------------------------------------------------");
		while (rs.next()) {
			System.out.println(" "+rs.getString("codHotel")+"  | "+rs.getString("nomHotel")+"  |       "+rs.getInt("numHabitacion")+"       |     "+rs.getInt("capacidad")+"     |     "+rs.getInt("preciodia")+"    |    "+rs.getInt("activa"));
		}
		System.out.println("---------------------------------------------------------------------------------");
		
		teclado.close();
	}

}
