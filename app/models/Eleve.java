/****************************************************************************

	ASCMII is a web application developped for the Ecole Centrale de Nantes
	aiming to organize quizzes during courses or lectures.
    Copyright (C) 2013  Malik Olivier Boussejra

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses/.

******************************************************************************/

package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Contient les élèves
 * @author Admin
 *
 */
@SuppressWarnings("serial")
@Entity
public class Eleve extends Model {
	@Id
	public String uid;
	
	@Required
	public String mail;
	@Required
	public String prenom;
	@Required
	public String nom;
	
	@OneToMany(targetEntity = Lien.class)
	public List<Lien> liens;
	@OneToMany(targetEntity = Repond.class)
	public List<Repond> repond;
	@OneToMany(targetEntity = Choisit.class)
	public List<Choisit> choisit;
	
	/**
	 * Crée un nouvel élève et l'enregistre dans la base de donnée
	 * @param uid_
	 * @param mail_
	 * @param prenom_
	 * @param nom_
	 */
	public Eleve(String uid_,String mail_,String prenom_,String nom_){
		uid=uid_;
		mail=mail_;
		prenom=prenom_;
		nom=nom_;
		save();
	}
	
	public static Finder<String,Eleve> find = new Finder<String,Eleve>(String.class, Eleve.class);

	public static List<Eleve> page(){
		return find.all();
	}
	
	public static void addEleve(Eleve el){
		el.save();
	}
	
	/**
	 * On doit supprimer d'autres éléments en cascade si on supprime un élève/
	 * @param ine
	 */
	public static void removeEleve(String uid){
		Eleve el = Eleve.find.byId(uid);
		if(el != null){
			List<Lien> ls = Lien.find.where().eq("eleve", el).findList();
			for(Lien l : ls){
				Lien.removeLien(l.chemin);
			}
			List<Repond> re = Repond.find.where().eq("eleve", el).findList();
			for(Repond r : re){
				Repond.removeRepond(r.id);
			}
			List<Choisit> ch = Choisit.find.where().eq("eleve", el).findList();
			for(Choisit c : ch){
				Choisit.removeChoisit(c.id);
			}
			el.delete();
		}
	}
	
}