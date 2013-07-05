package models;
import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.data.validation.Constraints.*;

//import java.sql.Timestamp;


@Entity
public class Seance extends Model {
	@Id
	public Long id;
	
	@Required
	public Date date;
	@Required
	public String matiere;
	@Required
	public String intitule;
	
	@ManyToOne
	public Professeur professeur;
	
	public static Finder<Long,Seance> find = new Finder<Long,Seance>(Long.class, Seance.class);

	public static List<Seance> page(){
		return find.orderBy("date").findList();
	}
	
	public static void addSeance(Seance se){
		se.save();
	}
	
	public static void removeItem(Long id){
		Seance se = Seance.find.ref(id);
		if(se != null){
			se.delete();
		}
	}
	
}