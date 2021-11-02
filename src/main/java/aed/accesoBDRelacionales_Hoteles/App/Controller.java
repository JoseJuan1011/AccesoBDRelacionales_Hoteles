package aed.accesoBDRelacionales_Hoteles.App;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import aed.accesoBDRelaciones_Hoteles.resources.Conectordb;

public class Controller {

	private Scanner teclado;
	
	private int tipoDB = 0;
	
	private int tipoAccion = 0;
	
	private int tipoProcedimiento = 0;
	
	private Connection connection = null;
	
	public Controller() {
		this(0,null);
	}
	
	public Controller(int tipoDB) {
		this(tipoDB,Conectordb.switchConnection(tipoDB));	
	}
	
	private Controller(int tipoDB, Connection connection) {
		teclado = new Scanner(System.in);
		this.tipoDB = tipoDB;
		tipoAccion = 0;
		tipoProcedimiento = 0;
		this.connection = connection;
	}
	
	public void execProgram() {
		System.out.println("Autor: José Juan Suárez González");
		System.out.println("Elija la base de datos con la que trabajar (Utilize solo números): ");
		System.out.println("-- 1-.MySQL \n-- 2-.SQL Server \n-- 3-.Acccess ");
		setTipoDB(teclado.nextInt());
		connection = Conectordb.switchConnection(getTipoDB());
		execOptions();
		teclado.close();
	}
	
	private void execOptions() {
		VisualizeTableHabitaciones();
		if (getTipoDB()!=3) {
			System.out.println("Elija la acción a tomar:\n"
					+ "1-. Insertar un registro \n"
					+ "2-. Modificar un registro \n"
					+ "3-. Eliminar un registro \n"
					+ "4-. Elegir un procedimiento"
			);
			setTipoAccion(teclado.nextInt());
			if (getTipoAccion()==4) {
				System.out.println("Elija el procedimiento a ejecutar:\n"
						+ "1-. Insertar una habitación con parámentros de salida.\n"
						+ "2-. Visualizar habitaciones según nombreHotel.\n"
						+ "3-. Visualizar cantidad de habitaciones normal y condicionado.\n"
						+ "4-. Visualizar suma total pagada por un cliente."
				);
				setTipoProcedimiento(teclado.nextInt());
			}
			execActions();
		}
		else {
			System.out.println("Elija la acción a tomar:\n"
					+ "1-. Insertar un registro \n"
					+ "2-. Modificar un registro \n"
					+ "3-. Eliminar un registro \n"
			);
			setTipoAccion(teclado.nextInt());
			execActions();
		}
		teclado.close();
	}
	
	private void execActions() {
		switch (tipoAccion) {
		case 3:
			Statements.EliminarAction(connection, tipoDB);
			
			System.out.println("¿Quiere seguir (S/N)?");
			String param = teclado.next();
			while (!(param.equals("S"))&&!(param.equals("N"))) {
				System.out.println("Elija una de las opciones permitidas (S/N): ");
				param = teclado.next();
			}
			
			if (param.equals("S")) {
				VisualizeTableHabitaciones();
				execOptions();
			}
			
		break;
		}
	}

	public void VisualizeTableHabitaciones() {
		try {
			PreparedStatement ps = connection.prepareStatement(""
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
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getTipoDB() {
		return tipoDB;
	}

	public void setTipoDB(int tipoDB) {
		this.tipoDB = tipoDB;
	}

	public int getTipoAccion() {
		return tipoAccion;
	}

	public void setTipoAccion(int tipoAccion) {
		this.tipoAccion = tipoAccion;
	}

	public int getTipoProcedimiento() {
		return tipoProcedimiento;
	}

	public void setTipoProcedimiento(int tipoProcedimiento) {
		this.tipoProcedimiento = tipoProcedimiento;
	}
	
	public void setConnection(int tipoDB) {
		connection = Conectordb.switchConnection(tipoDB);
	}
}
