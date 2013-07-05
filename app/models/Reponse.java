package models;
import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.data.validation.Constraints.*;


@Entity
public class Reponse extends Model {
	@Id
	public Long id;
	
	@Required
	public String texte;

	@ManyToOne
	public Question question;
	
	public static Finder<Long,Reponse> find = new Finder<Long,Reponse>(Long.class, Reponse.class);

	
	public static void addItem(Reponse re){
		re.save();
	}
	
	public static void removeItem(Long id){
		Reponse re = Reponse.find.ref(id);
		if(re != null){
			re.delete();
		}
	}
}