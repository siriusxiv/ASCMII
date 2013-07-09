package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Lien extends Model {
	@Id
	public String chemin;
	
	@OneToOne
	public Serie serie;
	@OneToOne
	public Eleve eleve;
	
	public static Finder<String,Lien> find = new Finder<String,Lien>(String.class, Lien.class);

	public static List<Lien> page(){
		return find.all();
	}
	
	public static void addLien(Eleve eleve, Serie serie){
		int a = eleve.hashCode();
		int b = serie.hashCode();
		int c = a+b;
		String chemin = Integer.toHexString(c);
		while(Lien.find.ref(chemin)!=null){
			c++;
			chemin=Integer.toHexString(c);
		}
		Lien lien = new Lien();
		lien.chemin = chemin;
		lien.serie=serie;
		lien.eleve=eleve;
		lien.save();
	}
	
	public static void removeLien(String chemin){
		Lien l = Lien.find.ref(chemin);
		if(l != null){
			l.delete();
		}
	}
	
}