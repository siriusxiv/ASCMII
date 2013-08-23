package functions;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.Eleve;
import functions.agap.AGAPStringUtil;
import functions.agap.Matiere;

/**
 * Contient les fonctions pour pouvoir se connecter à AGAP
 * @author malik
 *
 */
public class AGAPUtil {	
	private static String dbURL;
	private static String dbUser;
	private static String dbPass;

	public static List<Matiere> listMatieres;

	/**
	 * init data
	 */
	public static void init() {
		String dbDriver = play.Play.application().configuration().getString("agap.driver");
		String dbProtocol = play.Play.application().configuration().getString("agap.protocol");
		String dbHost = play.Play.application().configuration().getString("agap.host");
		String dbPort = play.Play.application().configuration().getString("agap.port");
		String dbName = play.Play.application().configuration().getString("agap.dbName");
		
		dbURL = dbProtocol + "://" + dbHost + ":" + dbPort + "/" + dbName;
		dbUser = play.Play.application().configuration().getString("agap.user");
		dbPass = play.Play.application().configuration().getString("agap.passw");
		try {
			// try with the main server
			System.out.println("Loading PostgreSQL driver...");
			Class.forName(dbDriver);
			System.out.println("Loading PostgreSQL driver... LOADED");
		} catch (ClassNotFoundException e) {
			Logger.getLogger(AGAPUtil.class.getName()).log(Level.INFO, null, e);
			System.out.println("Loading PostgreSQL driver... Failed");
		}

		getMatiereList();
	}

	/**
	 * Get connector to the database
	 */
	public static Connection getConnection() {
		Connection connection = null;
		try {
			System.out.println("Connecting to : " +dbURL+ " " + dbUser+" "+dbPass);
			connection = DriverManager.getConnection(dbURL, dbUser, dbPass);
		} catch (Exception e) {
			Logger.getLogger(AGAPUtil.class.getName()).log(Level.INFO, null, e);
			e.printStackTrace();
			
		}
		return connection;
	}

	/**
	 * Release connector to the database
	 */
	public static void releaseConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Décharge le driver postgres
	 */
	public static void release() {
		try {
			System.out.println("Discharging PostgreSQL driver...");
			Driver theDriver = DriverManager.getDriver(dbURL);
			DriverManager.deregisterDriver(theDriver);
			dbURL = null;
			System.out.println("Discharging PostgreSQL driver... DISCHARGED");
		} catch (SQLException ex) {
			Logger.getLogger(AGAPUtil.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Remplie la variable listMatieres avec la liste des matières dans AGAP
	 */
	private static void getMatiereList(){
		listMatieres = new ArrayList<Matiere>();
		/*listMatieres.add("ALGPR");
		listMatieres.add("GEMAT");
		listMatieres.add("SCUBE");
		listMatieres.add("dSIBAD");
		listMatieres.add("PEINS");
		listMatieres.add("CHEPA");
		listMatieres.add("GAGAG");
		listMatieres.add("AUTOM");
		listMatieres.add("CONEN");
		listMatieres.add("VIVRE");
		listMatieres.add("MATIE");*/
		String theQuery = listeCours();
		System.out.println("Connecting to AGAP...");
		Connection connection = getConnection();
		if(connection!=null){
			try {
				System.out.println("Executing : " + theQuery);
				Statement theStmt = connection.createStatement();
				ResultSet theRS1 = theStmt.executeQuery(theQuery);
				while (theRS1.next()) {
					Integer id = theRS1.getInt("ActionFormation_ID");
					String semestre = AGAPStringUtil.getSemestre(theRS1.getInt("ActionFormation_ID"),connection);
					listMatieres.add(new Matiere(theRS1.getString("actionformation_libellecourt"),
							theRS1.getString("actionformation_libellecourt"),semestre,
							id
							));
				}
			} catch (SQLException ex) {
				Logger.getLogger(AGAPUtil.class.getName()).log(Level.SEVERE, "query error " + theQuery, ex);
			}
			releaseConnection(connection);
		} else{
			System.out.println("Impossible de se connecter à AGAP...");
		}
	}
	
	/**
	 * Requête pour avoir la liste des cours
	 * @return
	 */
	private static String listeCours(){
		return "SELECT * FROM actionformation "
				+ "NATURAL JOIN cycle "
				+ "WHERE cycle_defaut=1";
	}

	/**
	 * Trouve les élèves suivant le cours au libellé indiqué
	 * @param libellecourt
	 * @return
	 */
	public static List<Eleve> getInscrits(Integer mat_id){
		List<Eleve> eleves = new ArrayList<Eleve>();
		String theQuery = listeInscrits(mat_id);
		System.out.println("Connecting to AGAP...");
		Connection connection = getConnection();
		if(connection!=null){
			try {
				System.out.println("Executing : " + theQuery);
				Statement theStmt = connection.createStatement();
				ResultSet theRS1 = theStmt.executeQuery(theQuery);
				while (theRS1.next()) {
					String uid = theRS1.getString("personne_uid");
					Eleve eleve = Eleve.find.byId(uid);
					if(eleve==null){
						System.out.println("uid not found : " + uid);
					}else{
						eleves.add(eleve);
					}
				}
			} catch (SQLException ex) {
				Logger.getLogger(AGAPUtil.class.getName()).log(Level.SEVERE, "query error " + theQuery, ex);
			}
			releaseConnection(connection);
		} else{
			System.out.println("Impossible de se connecter à AGAP...");
		}
		return eleves;
	}
	
	/**
	 * Requête pour avoir la liste des inscrits
	 * @param ActionFormation_ID
	 * @return
	 */
	private static String listeInscrits(Integer ActionFormation_ID){
		return "SELECT * FROM InscriptionAction "
				+ "NATURAL JOIN Inscription "
				+ "NATURAL JOIN CursusPrepare "
				+ "NATURAL JOIN DossierScolaire "
				+ "NATURAL JOIN Eleve "
				+ "NATURAL JOIN Personne "
				+ "WHERE ActionFormation_ID="+ActionFormation_ID;
	}

	/**
	 * Requête pour avoir la liste des inscrits
	 * /!\ ascmii n'a pour l'instant pas les droits pour accéder à GroupeStructure... 
	 * @param ActionFormation_ID
	 * @return
	 */
	public static String listeInscritsGroupe(Integer ActionFormation_ID){
		return "SELECT * FROM InscriptionAction "
				+ "NATURAL JOIN Inscription "
				+ "NATURAL JOIN CursusPrepare "
				+ "NATURAL JOIN DossierScolaire "
				+ "NATURAL JOIN Eleve "
				+ "NATURAL JOIN Personne "
				+ "NATURAL JOIN EleveDansGroupe "
				+ "NATURAL JOIN Groupe "
				+ "NATURAL JOIN GroupeStructure "
				+ "NATURAL JOIN Structures "
				+ "WHERE ActionFormation_ID="+ActionFormation_ID;
	}
}
