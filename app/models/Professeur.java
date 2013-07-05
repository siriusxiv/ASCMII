package models;
import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.data.validation.Constraints.*;


@Entity
public class Professeur extends Model {
	@Id
	public String username;
	
	public static Finder<String,Professeur> find = new Finder<String,Professeur>(String.class, Professeur.class);


	public static List<Professeur> page(){
		return find.all();
	}
	
	public static void addItem(Professeur pr){
		pr.save();
	}
	
	public static void removeItem(String username){
		Professeur pr = Professeur.find.ref(username);
		if(pr != null){
			pr.delete();
		}
	}
	
}