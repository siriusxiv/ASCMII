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

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import functions.Numbers;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Contient les liens unique entre chaque élève et chaque série.
 * Un lien relie un élève et une série car chaque série a une URL unique pour
 * chaque élève.
 * @author Admin
 *
 */
@SuppressWarnings("serial")
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
	 * Utilisé pour les tests unitaires dans Tests.java, ne l'utilisez pas ailleurs !
	 */
	public Lien(Serie serie_, Eleve eleve_){
		chemin = Integer.toHexString(Numbers.randomInt());
		while(find.byId(chemin)!=null){
			chemin=Integer.toHexString(Numbers.randomInt());
		}
		serie=serie_;
		eleve=eleve_;
		repondu=false;
	}
	
	/**
	 * Constructeur standard
	 * @param chemin_ : fin de l'url d'accès
	 * @param s : série
	 * @param e : élève
	 */
	private Lien(String chemin_, Serie s, Eleve e){
		chemin=chemin_;
		serie=s;
		eleve=e;
		repondu=false;
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
			String chemin = Integer.toHexString(Numbers.randomInt());
			while(find.byId(chemin)!=null){
				chemin=Integer.toHexString(Numbers.randomInt());
			}
			Lien lien = new Lien(chemin,serie,eleve);
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