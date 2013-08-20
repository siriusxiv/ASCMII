package functions;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;



public class AGAPUtil {
	private static String dbURL;
	private static String dbUser;
	private static String dbPass;

	/**
	 * init data
	 */
	public static void init() {
		 ResourceBundle res = ResourceBundle.getBundle("functions.databaseConfig");
	        
         String dbDriver = getResourceElement(res, "driver", "org.postgresql.Driver");
         String dbProtocol = getResourceElement(res, "protocol", "jdbc:postgresql");
         String dbHost = getResourceElement(res, "dbhost", "");
         String dbPort = getResourceElement(res, "dbport", "5432");
         String dbName = getResourceElement(res, "dbname", "");
         
         dbURL = dbProtocol + "://" + dbHost + ":" + dbPort + "/" + dbName;
         dbUser = getResourceElement(res, "dbuser", "");
         dbPass = getResourceElement(res, "dbpass", "");

		try {
			// try with the main server
			Class.forName(dbDriver);
		} catch (Exception e) {
			Logger.getLogger(AGAPUtil.class.getName()).log(Level.INFO, null, e);
		}
	}
	
	 /**
     * Get connector to the database
     */
     public static Connection getConnection() {
             Connection connection = null;
             try {
                     connection = DriverManager.getConnection(dbURL, dbUser, dbPass);
             } catch (Exception e) {
                     Logger.getLogger(AGAPUtil.class.getName()).log(Level.INFO, null, e);
             }
             
             return connection;
     }

     /**
      * Décharge le driver postgres
      */
	public static void release() {
		try {
			Driver theDriver = DriverManager.getDriver(dbURL);
			DriverManager.deregisterDriver(theDriver);
			dbURL = null;
		} catch (SQLException ex) {
			Logger.getLogger(AGAPUtil.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	/**
     * Get a String configuration from the resource file
     *
     * @param res
     * @param element
     * @param defaultValue
     * @return
     */
    private static String getResourceElement(ResourceBundle res, String element, String defaultValue) {
        String newValue;
        String returnValue;

        returnValue = defaultValue;
        if (res != null) {
            try {
                newValue = res.getString(element);
                if (!newValue.equals("")) {
                    returnValue = newValue;
                }
            } catch (Exception e) {
            }
        }
        return returnValue;
    }
}
