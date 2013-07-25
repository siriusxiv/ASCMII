package models;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

/**
 * Contient les liens unique entre chaque élève et chaque série.
 * Un lien relie un élève et une série car chaque série a une URL unique pour
 * chaque élève.
 * @author Admin
 *
 */
@Entity
public class Lien extends Model {
	@Id
	public String chemin;
	@Required
	public boolean repondu;
	
	@OneToOne
	public Serie serie;
	@OneToOne
	public Eleve eleve;
	
	public static Finder<String,Lien> find = new Finder<String,Lien>(String.class, Lien.class);

	public static List<Lien> page(){
		return find.all();
	}
	
	/**
	 * Ajoute un lien dans la base de donnée à partir d'un élève et d'une série.
	 * Le chemin du lien doit avoir l'être aléatoire. On génère ce chemin avec les
	 * hashCode des classes Eleve et Serie convertis en chaîne hexadécimale.
	 * @param eleve
	 * @param serie
	 */
	public static void addLien(Eleve eleve, Serie serie){
		if(Lien.find.where().eq("eleve",eleve).eq("serie",serie).findList().isEmpty()){//Si un tel lien exist déjà, pas besoin de le rajouter
			int a = eleve.hashCode();
			int b = serie.hashCode();
			int c = a*b;
			String chemin = Integer.toHexString(c);
			while(find.byId(chemin)!=null){
				c*=2;
				chemin=Integer.toHexString(c);
			}
			Lien lien = new Lien();
			lien.chemin = chemin;
			lien.serie=serie;
			lien.eleve=eleve;
			lien.repondu=false;
			lien.save();
		}
	}
	
	/**
	 * Vérifie que, pour une lien donnée, il n'est pas trop tard pour répondre à la question
	 * @return Vrai ou Faux
	 */
	public boolean aLHeure(){
		return (serie.date_ouverte!=null &&
					(serie.date_fermeture==null ||
						Calendar.getInstance().getTime().compareTo(serie.date_fermeture)<=0
					)
				);
	}
	
	public static void removeLien(String chemin){
		Lien l = Lien.find.byId(chemin);
		if(l != null){
			l.delete();
		}
	}
	
}