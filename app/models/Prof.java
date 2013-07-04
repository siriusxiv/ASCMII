package models;
import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

public class Prof extends Model{
	//Attributes
	public String login;
	public String passw;
	
	//Hardcoded values (for illustrating purposes)
	private final static String correctLogin="admin";
	private final static String correctPassw="admin";
	
	//Validation method
	public String validate(){
		if(login.equals(correctLogin) && passw.equals(correctPassw)){
			return null;
		}else{
			return "Invalid login or password";
		}
	}
}