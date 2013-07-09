package models;

import java.util.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Repond extends Model {
	@Id
	public Long id;
	
	@Required
	public String texte;
	@Required
	public Date date;
	
	@OneToOne
	public Question question;
	@OneToOne
	public Eleve eleve;
	
	public static Finder<Long,Repond> find = new Finder<Long,Repond>(Long.class, Repond.class);

	public static List<Repond> page(){
		return find.all();
	}
	
	public static void addRepond(Repond r){
		r.save();
	}
	
	public static void removeRepond(Long id){
		Repond r = Repond.find.byId(id);
		if(r != null){
			r.delete();
		}
	}
	
}