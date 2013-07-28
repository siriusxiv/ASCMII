package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Fait le lien entre un élève et la réponse qu'il choisit (pour une question de type 1 ou 2).
 * @author Admin
 *
 */
@Entity
public class Choisit extends Model {
	@Id
	public Long id;
	
	@Required
	public Date date;
	
	@OneToOne
	public Reponse reponse;
	@OneToOne
	public Eleve eleve;
	
	public static Finder<Long,Choisit> find = new Finder<Long,Choisit>(Long.class, Choisit.class);

	public static List<Choisit> page(){
		return find.all();
	}
	
	public static void addChoisit(Choisit c){
		c.save();
	}
	
	public static void removeChoisit(Long id){
		Choisit c = Choisit.find.byId(id);
		if(c != null){
			c.delete();
		}
	}
	
}