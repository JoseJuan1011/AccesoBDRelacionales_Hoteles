package aed.accesoBDRelacionales_Hoteles.App;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

public class Statements {
	
	private static Connection conn;
	
	private static Scanner teclado;
	
	private static String codHotel;
	
	private static int numHabitacion, capacidad, preciodia, activa;
	
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
			System.out.println("| "+codHotel+"  |       "+numHabitacion+"       |     "+capacidad+"     |     "+preciodia+" | "+activa);
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
			ps.executeUpdate();
			
			System.out.println("Insercción Completada");
		} catch (SQLException e) {
			System.out.println("Insercción No Completada");
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

	//Modificar Action
	public static void ModificarAction(Connection connection) {
		teclado = new Scanner(System.in);
		conn = connection;
		
		System.out.println("Escriba los datos del registro a modificar: ");
		System.out.print("codHotel: ");
		codHotel = teclado.next();
		System.out.print("numHabitación: ");
		numHabitacion = teclado.nextInt();
		
		if (mostrarRegistroAModificar()) {
			int[] datos = new int[3];
			int i = 0;
			System.out.println("Escriba aquí los valores a modificar (si no quiere modificar el valor, dele a Enter): ");
			String camposAModificar="";
			String capacidadNuevo,preciodiaNuevo,activaNuevo;
			boolean capacidadModificar, preciodiaModificar, activaModificar;
			System.out.print("capacidad: ");
			teclado.nextLine();
			capacidadNuevo = teclado.nextLine();
			if(capacidadModificar=validarInt(capacidadNuevo)) {
				camposAModificar+="capacidad = ?;";
				datos[i] = Integer.parseInt(capacidadNuevo);
				i++;
			}
			System.out.print("preciodia: ");
			preciodiaNuevo = teclado.nextLine();
			if (preciodiaModificar=validarInt(preciodiaNuevo)) {
				camposAModificar+="preciodia = ?;";
				datos[i] = Integer.parseInt(preciodiaNuevo);
				i++;
			}
			
			System.out.print("activa: ");
			activaNuevo = teclado.nextLine();
			if (activaModificar=validarInt(activaNuevo)) {
				camposAModificar+="activa = ?";
				datos[i] = Integer.parseInt(activaNuevo);
				i++;
			}
			
			camposAModificar = camposAModificar.replaceAll(";", ",");
			
			try {
				int j = 1;
				PreparedStatement ps = conn.prepareStatement("Update habitaciones set "+camposAModificar+" where codHotel= ? and numHabitacion=?");
				if (capacidadModificar) {
					ps.setInt(j, datos[j-1]);
					j++;
				}
				if (preciodiaModificar) {
					ps.setInt(j, datos[j-1]);
					j++;
				}
				if (activaModificar) {
					ps.setInt(j, datos[j-1]);
					j++;
				}
				ps.setString(j, codHotel);
				j++;
				ps.setInt(j, numHabitacion);
				ps.executeUpdate();
				
				System.out.println("Modificación Realizada");
			} catch (SQLException e) {
				System.out.println("Modificación no Realizada");
			}
		}
		else {
			System.out.println("No existe tal registro en la base de datos");
		}
	}
	
	private static boolean validarInt(String num) {
		try {
			Integer.parseInt(num);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	private static boolean mostrarRegistroAModificar() {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM HABITACIONES where codHotel=? and numHabitacion=?");
			ps.setString(1, codHotel);
			ps.setInt(2, numHabitacion);
			
			ResultSet rs = ps.executeQuery();
			System.out.println("¿Está seguro que quiere modificar el siguiente registro (S/N)?");
			System.out.println("codHotel | numHabitacion | capacidad | preciodia | activa");
			System.out.println("-----------------------------------------------------------");
			while (rs.next()) {
				System.out.println(" "+rs.getString("codHotel")+"  |       "+rs.getInt("numHabitacion")+"       |     "+rs.getInt("capacidad")+"     |     "+rs.getInt("preciodia")+" | "+rs.getInt("activa"));
			}
			
			return switchBooleano();
		} catch (SQLException e) {
			System.out.println("No se pudo mostrar el registro a Modificar");
			return false;
		}
	}

	
	private static boolean switchBooleano() {
		String param = teclado.next();
		switch (param) {
		case "S":
			return true;
		case "N":
			return false;
		default:
			while (!(param.equals("S"))&&!(param.equals("N"))) {
				System.out.println("Elija una de las opciones permitidas (S/N): ");
				param = teclado.next();
			}
			return switchBooleano();
		}
	}

	public static void EliminarAction(Connection connTypeBD, int tipoDB) {
		teclado = new Scanner(System.in);
		conn = connTypeBD;
		System.out.println("Escriba los datos del registro a Eliminar: ");
		System.out.print("codHotel: ");
		String codHotel = teclado.nextLine();
		System.out.print("numHabitacion: ");
		int numHabitacion = teclado.nextInt();
		if (mostrarRegistroAEliminar(codHotel, numHabitacion)) {
			
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
				System.out.println("Eliminación Incompleta");
			}
			
		}
		else {
			System.out.println("No existe tal registro en la base de datos");
		}	
	}
	
	private static boolean mostrarRegistroAEliminar(String codHotel, int numHabitacion) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM HABITACIONES where codHotel=? and numHabitacion=?");
			ps.setString(1, codHotel);
			ps.setInt(2, numHabitacion);
			
			ResultSet rs = ps.executeQuery();
			System.out.println("¿Está seguro que quiere eliminar el siguiente registro (S/N)?");
			System.out.println("codHotel | numHabitacion | capacidad | preciodia | activa");
			System.out.println("-----------------------------------------------------------");
			while (rs.next()) {
				System.out.println(" "+rs.getString("codHotel")+"  |       "+rs.getInt("numHabitacion")+"       |     "+rs.getInt("capacidad")+"     |     "+rs.getInt("preciodia")+" | "+rs.getInt("activa"));
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
			System.out.println("No se pudo mostrar el registro a eliminar");
			return false;
		}
	}

	
	public static void ProcedimientosAction(Connection connection, int tipoDB, int tipoProcedimiento) {
		try {
			switch(tipoProcedimiento) {
			
			case 1:
				Procedimiento1(connection, tipoDB);
			break;
			
			case 2:
				Procedimiento2(connection, tipoDB);
			break;
			
			case 3:
				Procedimiento3(connection, tipoDB);
			break;
			
			case 4:
				Procedimiento4(connection, tipoDB);
			break;
			}
		}
		catch (SQLException e) {
			System.out.println("No se pudo realizar el procedimiento en cuestión");
		}
	}
	
	private static void Procedimiento1(Connection connection, int tipoDB) throws SQLException {
		teclado = new Scanner(System.in);
		System.out.println("Escriba el nombre de Hotel a visualizar: ");
		String nomHotel = teclado.next();
		conn = connection;
		PreparedStatement ps;
		if (tipoDB==2) {
			ps = conn.prepareStatement("exec procHabitacionesHotel @nomHotel = ?");
			ps.setEscapeProcessing(true);
	        ps.setQueryTimeout(0);
		}
		else {
			ps = conn.prepareStatement("call proc_habitaciones_hotel (?);");
		}
		ps.setString(1, nomHotel);
        ResultSet rs = ps.executeQuery();
        System.out.println("numHabitacion | capacidad | preciodia | activa");
        System.out.println("----------------------------------------------");
        while (rs.next()) {
        	System.out.println("      "+rs.getString(1).trim()+"       |     "+rs.getInt(2)+"     |    "+rs.getInt(3)+"     |    "+rs.getInt(4));
        }
        System.out.println("----------------------------------------------");
	}

	private static void Procedimiento2(Connection connection, int tipoDB) throws SQLException {
		PreparedStatement MySQL_PS;
		PreparedStatement paramPS;
		CallableStatement SQLServerPS;
		teclado = new Scanner(System.in);
		conn = connection;
		System.out.println("Escriba aquí los datos del registro a insertar: ");
		System.out.print("codHotel, aquí las opciones -> " + codHotelComboBox() + ": ");
		codHotel = teclado.next();
		System.out.print("numHabitación: ");
		numHabitacion = teclado.nextInt();
		System.out.print("capacidad: ");
		capacidad = teclado.nextInt();
		System.out.print("preciodia: ");
		preciodia = teclado.nextInt();
		System.out.print("activa: ");
		activa = teclado.nextInt();
		if (tipoDB==2) {
			SQLServerPS = (CallableStatement) conn.prepareCall("EXEC proc_insert_habitacion ?,?,?,?,?,?,?");
			SQLServerPS.setString(1, codHotel);
			SQLServerPS.setInt(2, numHabitacion);
			SQLServerPS.setInt(3, capacidad);
			SQLServerPS.setInt(4, preciodia);
			SQLServerPS.setInt(5, activa);
			
			SQLServerPS.registerOutParameter(6, Types.INTEGER);
			SQLServerPS.registerOutParameter(7, Types.INTEGER);
			
			SQLServerPS.execute();
			
			System.out.println(SQLServerPS.getInt(6) == 1 ? "El hotel existe" : "El hotel NO existe");
			System.out.println(SQLServerPS.getInt(7) == 1 ? "Inserccion Correcta" : "Algo malo ocurrio...");
		}
		else {
			MySQL_PS = conn.prepareStatement("call proc_insert_habitacion (?, ?, ?, ?, ?, @hotel_exists, @validate_insert);");
			
			MySQL_PS.setString(1, codHotel);
			MySQL_PS.setInt(2, numHabitacion);
			MySQL_PS.setInt(3, capacidad);
			MySQL_PS.setInt(4, preciodia);
			MySQL_PS.setInt(5, activa);
			MySQL_PS.executeQuery();
			
			paramPS = conn.prepareStatement("Select @hotel_exists, @validate_insert");
			
			ResultSet rs = paramPS.executeQuery();
			
			while (rs.next()) {
				System.out.println(rs.getInt(1)== 1 ? "El hotel existe" : "El hotel NO existe"+", "+rs.getInt(2));
				System.out.println(rs.getInt(2)== 1 ? "Todo se realizo con normalidad" : "No se ha podido realizar la insercción con normalidad");
			} 
		}
	}
	
	private static void Procedimiento3(Connection connection, int tipoDB) throws SQLException {
		teclado = new Scanner(System.in);
		conn = connection;
		System.out.println("Escriba los parámetros aquí: ");
		System.out.print("nomHotel: ");
		String nomHotel = teclado.nextLine();
		System.out.print("preciodia: ");
		preciodia = teclado.nextInt();
		
		ResultSet rs;
		if (tipoDB==2) {
			PreparedStatement ps = conn.prepareStatement(""
					+ "declare @cantidadTotal tinyint, @cantidadTotal_preciodia tinyint\r\n"
					+ "exec proc_cantidad_habitaciones ?, ?, @cantidadTotal output, @cantidadTotal_preciodia output\r\n"
					+ "select @cantidadTotal as CantidadTotal, @cantidadTotal_preciodia as CantidadTotal_preciodia"
			);
			ps.setString(1, nomHotel);
			ps.setInt(2, preciodia);
			
			rs = ps.executeQuery();
		}
		else {
			PreparedStatement ps = conn.prepareStatement("call proc_cantidad_habitaciones (?, ?, @cantidadTotal, @cantidadTotal_preciodia);");
			ps.setString(1, nomHotel);
			ps.setInt(2, preciodia);
			ps.executeQuery();
			
			PreparedStatement paramPS = conn.prepareStatement("select @cantidadTotal as CantidadTotal, @cantidadTotal_preciodia as CantidadTotal_preciodia;");
			paramPS.executeQuery();
			
			rs = paramPS.executeQuery();
		}
		while (rs.next()) {
			System.out.println("CantidadTotal de habitaciones del hotel: "+rs.getInt(1));
			System.out.println("CantidadTotal de habitaciones del hotel, cuales preciodia sean menor que el pasado y esten activas: "+rs.getInt(2));
		}
	}
	
	private static void Procedimiento4(Connection connection, int tipoDB) throws SQLException {
		teclado = new Scanner(System.in);
		conn = connection;
		PreparedStatement ps;
		System.out.println("DNI del Cliente: ");
		String DNI = teclado.next();
		if (tipoDB==2) {
			ps = conn.prepareStatement(""
					 + "Declare @CantidadEstancias int" + "\n"
                     + "exec @CantidadEstancias = dbo.sp_dni_suma  ?\n"
                     + "select @CantidadEstancias as CantidadTotal"
			);	
		}
		else {
			ps = conn.prepareStatement("select sp_dni_suma(?) as CantidadTotal;");
		}
		ps.setString(1, DNI);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			System.out.println("Cantidad Pagada por el Cliente en cuestión: "+rs.getInt("CantidadTotal"));
		}
	}
}
