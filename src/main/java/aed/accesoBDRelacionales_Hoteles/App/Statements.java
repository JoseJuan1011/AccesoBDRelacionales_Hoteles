package aed.accesoBDRelacionales_Hoteles.App;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Statements {
	
	private static Connection conn;
	
	private static Scanner teclado;
	
	private static String codHotel;
	
	private static int numHabitacion, capacidad, preciodia, activa;
	
	public static void EliminarAction(Connection connTypeBD, int tipoDB) {
		teclado = new Scanner(System.in);
		conn = connTypeBD;
		System.out.println("Escriba los datos del registro a Eliminar: ");
		System.out.print("codHotel: ");
		String codHotel = teclado.nextLine();
		System.out.print("numHabitacion: ");
		int numHabitacion = teclado.nextInt();
		if (mostrarRegistro(codHotel, numHabitacion)) {
			
			System.out.println("¿Quiere Eliminar las Estancias dependientes también (S/N)?");
			String param = teclado.next();
			while (!(param.equals("S"))&&!(param.equals("N"))) {
				System.out.println("Elija una de las opciones permitidas (S/N): ");
				param = teclado.next();
			}
			
			try {
				if (param.equals("S")) {
					PreparedStatement deletePs = connTypeBD.prepareStatement("Delete from estancias where codHotel = ? and numHabitacion = ?;");
					deletePs.setString(1, codHotel);
					deletePs.setInt(2, numHabitacion);
					deletePs.executeUpdate();
				}
				else {
					PreparedStatement updatePs = connTypeBD.prepareStatement("Update estancias set codHotel = null, numHabitacion = null where codHotel = ? and numHabitacion = ?;");
					updatePs.setString(1, codHotel);
					updatePs.setInt(2, numHabitacion);
					updatePs.executeUpdate();
				}
				
				PreparedStatement ps = connTypeBD.prepareStatement("Delete from habitaciones where codHotel=? and numHabitacion=?");
				ps.setString(1, codHotel);
				ps.setInt(2, numHabitacion);
				ps.executeUpdate();
				
				System.out.println("Eliminación Completada");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		else {
			System.out.println("No existe tal registro en la base de datos");
		}	
	}
	
	private static boolean mostrarRegistro(String codHotel, int numHabitacion) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM HABITACIONES where codHotel=? and numHabitacion=?");
			ps.setString(1, codHotel);
			ps.setInt(2, numHabitacion);
			
			ResultSet rs = ps.executeQuery();
			System.out.println("¿Está seguro que quiere eliminar el siguiente registro (S/N)?");
			System.out.println("codHotel | numHabitacion | capacidad | preciodia | activa");
			System.out.println("-----------------------------------------------------------");
			while (rs.next()) {
				System.out.println(" "+rs.getString("codHotel")+"  |       "+rs.getInt("numHabitacion")+"       |     "+rs.getInt("capacidad")+"     |     "+rs.getInt("preciodia")+"    |    "+rs.getInt("activa"));
			}
			System.out.println("-----------------------------------------------------------");
			switch (teclado.next()) {
			case "S":
				return true;
				
			case "N":
				return false;
				
			default:
				System.out.println("No se escribió un parámetro valido");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	
	public static void InsertarAction(Connection connection) {
		teclado = new Scanner(System.in);
		conn = connection;
		
		System.out.println("Escriba aquí los datos del registro a insertar: ");
		try {
			System.out.print("codHotel, aquí las opciones -> "+codHotelComboBox()+": ");
			codHotel = teclado.next();
			System.out.print("numHabitación: ");
			numHabitacion = teclado.nextInt();
			System.out.print("capacidad: ");
			capacidad = teclado.nextInt();
			System.out.print("preciodia: ");
			preciodia = teclado.nextInt();
			System.out.print("activa: ");
			activa = teclado.nextInt();
			System.out.println("¿Está seguro que quiere insertar el siguiente registro (S/N)?: ");
			System.out.println("codHotel | numHabitacion | capacidad | preciodia | activa");
			System.out.println("---------------------------------------------------------");
			System.out.println("| "+codHotel+" | "+numHabitacion+" | "+capacidad+" | "+preciodia+" | "+activa);
			System.out.println("---------------------------------------------------------");
			String param = teclado.next();
			while (param.equals("N")&&!param.equals("S")) {
				if (!param.equals("N")) {
					while (!(param.equals("S"))&&!(param.equals("N"))) {
						System.out.println("Elija una de las opciones permitidas (S/N): ");
						param = teclado.next();
					}
				}
				InsertarAction(conn);
			}
			
			PreparedStatement ps = conn.prepareStatement("Insert into habitaciones values (?,?,?,?,?)");
			ps.setString(1, codHotel);
			ps.setInt(2, numHabitacion);
			ps.setInt(3,capacidad);
			ps.setInt(4, preciodia);
			ps.setInt(5, activa);
			ps.executeQuery();
			
			System.out.println("Insercción Completada");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static String codHotelComboBox() throws SQLException {
		String aux = " | ";
		
		PreparedStatement ps = conn.prepareStatement("Select codHotel from hoteles");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			aux+=rs.getString(1)+" | ";
		}
		return aux;
	}
}
