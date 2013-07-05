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
	public Date date;
	@Required
	public String matiere;
	@Required
	public String intitule;
	
	@ManyToOne
	public Professeur professeur;
	
	public static Finder<Long,Seance> find = new Finder<Long,Seance>(Long.class, Seance.class);

	public static List<Seance> page(HashMap<String,String> session){
		Professeur prof = Professeur.find.ref(session.get("username"));
		return find
				.where()
					.eq("professeur",prof)
				.orderBy("date")
				.findList();
	}
	
	public static void addSeance(Seance se){
		se.save();
	}
	
	public static void removeSeance(Long id){
		Seance se = Seance.find.ref(id);
		if(se != null){
			se.delete();
		}
	}
	
}