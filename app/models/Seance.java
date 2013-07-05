package models;
import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.data.validation.Constraints.*;


@Entity
public class Seance extends Model {
	@Id
	public Long id;
	
	@Required
	public String nom;
	@Required
	public Long ouverte;
	
	@ManyToOne
	public Professeur professeur;
	
	public static Finder<Long,Seance> find = new Finder<Long,Seance>(Long.class, Seance.class);

	
	public static void addItem(Seance se){
		se.save();
	}
	
	public static void removeItem(Long id){
		Seance se = Seance.find.ref(id);
		if(se != null){
			se.delete();
		}
	}
	
}