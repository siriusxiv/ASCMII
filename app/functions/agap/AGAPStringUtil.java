package functions.agap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Des fonctions utiles pour gérer les données qui sortent d'AGAP
 * @author Admin
 *
 */
public class AGAPStringUtil {

	/**
	 * Obtient l'uid à partir de personne_dn (inutile pour l'instant)
	 * @param dn : coordonnées dans LDAP de la personne
	 * @return uid
	 */
	public static String getUID(String dn){
		return dn.substring(4, dn.indexOf(','));
	}

	/**
	 * Renvoie le semestre d'une certaine matière sous la forme d'une string : S#.
	 * @param actionformation_id : id de la matière dans AGAP
	 * @param connection : Connection à la base de donnée
	 * @return String : S5, S6 ou S7, etc.
	 */
	public static String getSemestre(Integer actionformation_id,Connection connection){
		String semestre = "";
		String theQuery = "SELECT structures_id,typestructure_id "
				+ "FROM actionstructure "
				+ "NATURAL JOIN structures "
				+ "WHERE actionformation_id="+actionformation_id;
		try {
			System.out.println("Executing : " + theQuery);
			Statement theStmt = connection.createStatement();
			ResultSet theRS1 = theStmt.executeQuery(theQuery);
			while (theRS1.next()) {
				Integer typestructure_id = theRS1.getInt("typestructure_id");
				Integer structure_id = theRS1.getInt("structures_id");
				int i=0;
				while(typestructure_id!=16 && typestructure_id!=1 && typestructure_id!=10){
					theQuery = "SELECT structures_id,typestructure_id "
							+ "FROM structuresliens "
							+ "INNER JOIN structures ON(structures.structures_id=structuresliens.structures_pere_id) "
							+ "WHERE structures_fils_id="+structure_id;
					theStmt = connection.createStatement();
					ResultSet theRS = theStmt.executeQuery(theQuery);
					while(theRS.next()){
						System.out.println("NEXT:" + structure_id+" "+typestructure_id+" : "+i);
						typestructure_id=theRS.getInt("typestructure_id");
						structure_id = theRS.getInt("structures_id");
					}
					i++;
					System.out.println(structure_id+" "+typestructure_id+" : "+i);
				}
				theQuery = "SELECT structures_libelle,structures_libellecourt "
						+ "FROM structures "
						+ "WHERE structures_id="+structure_id;
				theStmt = connection.createStatement();
				ResultSet theRS = theStmt.executeQuery(theQuery);
				while(theRS.next()){
					String sem = theRS.getString("structures_libelle");
					semestre = toLibelleSemestreCourt(sem);
					System.out.println(semestre);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return semestre;
	}
	
	/**
	 * Convertit la string du semestre stocké dans AGAP à la forme voulue
	 * (S5,S6,S7,etc...)
	 * @param sem : semestre stocké dans AGAP sous forme de string
	 * @return String : S5, S6 ou S7, etc.
	 */
	private static String toLibelleSemestreCourt(String sem){
		if(sem.startsWith("Semestre ")){
			return "S"+sem.substring(9);
		}else{
			return "";
		}
	}
}
