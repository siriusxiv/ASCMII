package models;
import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.data.validation.Constraints.*;


@Entity
public class Serie extends Model {
	@Id
	public Long id;
	
	@Required
	public String nom;
	@Required
	public Long ouverte;
	
	@ManyToOne
	public Seance seance;
	
	public static Finder<Long,Serie> find = new Finder<Long,Serie>(Long.class, Serie.class);

	
	public static void addItem(Serie se){
		se.save();
	}
	
	public static void removeItem(Long id){
		Serie se = Serie.find.ref(id);
		if(se != null){
			se.delete();
		}
	}
}