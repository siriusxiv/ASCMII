package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Column;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Contient les réponses d'un élève à une question de type 3 ou 4.
 * @author Admin
 *
 */
@Entity
public class Repond extends Model {
	@Id
	public Long id;
	
	@Required
	@Column(columnDefinition = "TEXT")
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
	
	/**
	 * Vérifie si la réponse est déjà dans une liste de couple "Répond,Integer".
	 * Voir CoupleRI pour plus de détails.
	 * @param list
	 * @return La position de la réponse dans la liste si elle est déjà présente, -1 sinon.
	 */
	public int isIn(List<CoupleRI> list){
		int i=0;
		while(i<list.size()){
			if(list.get(i).repond.texte.equals(texte)){
				return i;
			}
			i++;
		}
		return -1;
	}
}