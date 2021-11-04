package aed.accesoBDRelaciones_Hoteles.resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class TrialConnectionsMain {

	public static void main(String[] args) throws SQLException {
		String sinTrim = "capacidad=?;preciodia=?;";
		String[] s = sinTrim.split(";");
		for (int i = 0; i<s.length ; i++) {
			System.out.println(s[i]);
		}
	}

}
