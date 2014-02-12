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

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import functions.ParseDate;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;


/**
 * Contient les séances
 * @author Admin
 *
 */
@SuppressWarnings("serial")
@Entity
public class Seance extends Model {
	@Id
	public Long id;
	
	@Required
	public Date date;
	@Required
	public String matiere;
	@Required
	public Integer matiere_id;
	@Required
	public String intitule;
	
	public String groupe;
	
	@ManyToOne
	public EleveGroupe custom_group;
	
	@ManyToOne
	public Professeur professeur;
	
	@OneToMany(targetEntity = Serie.class)
	public List<Serie> series;
	
	/**
	 * Constructeur par défaut. Ne fait rien (il est là pour compatibilité avec du code
	 * qui a été écrit à un moment où je pensais qu'il était inutile d'en construire un.
	 */
	public Seance(){}
	
	/**
	 * Crée une séance sans encore spécifier la matière. Il faudra la spécifier plus tard.
	 * On choisir une date par défaut très très loin dans le futur (la dernière seconde du temps
	 * UNIX sur un int de 32 bit, juste pour rigoler).
	 * @param username
	 * @param _intitule
	 */
	public Seance(String username, String _intitule){
		id=idNonUtilisee();
		intitule=_intitule;
		professeur=Professeur.find.ref(username);
		date=ParseDate.lastDate();
		custom_group=null;
	}
	
	/**
	 * Crée une séance semblable à la séance à dupliquer mais en changeant la date
	 * @param seanceADupliquer
	 */
	public Seance(Seance seanceADupliquer){
		intitule=seanceADupliquer.intitule;
		matiere=seanceADupliquer.matiere;
		matiere_id=seanceADupliquer.matiere_id;
		professeur=seanceADupliquer.professeur;
		date=ParseDate.lastDate();
		//On choisit l'ID de la prochaine nouvelle Séance
		id=Seance.idNonUtilisee();
		custom_group=null;
	}
	/**
	 * Teste si la séance a une série vide à l'intérieur
	 * @return VRAI ou FAUX
	 */
	public boolean hasEmptySerie(){
		boolean result=false;
		for(Serie s : series){
			result=s.questions.isEmpty();
		}
		return result;
	}
	
	/**
	 * Définit le finder pour cette table
	 */
	public static Finder<Long,Seance> find = new Finder<Long,Seance>(Long.class, Seance.class);

	/**
	 * Renvoie la liste des séances sous la responsabilité du professeur connecté,
	 * c'est-à-dire celui dont le nom est dans la session.
	 * @param session
	 * @return Liste de séance
	 */
	public static List<Seance> page(HashMap<String,String> session){
		Professeur prof = Professeur.find.ref(session.get("username"));
		return find
				.where()
					.eq("professeur",prof)
				.orderBy("date desc")
				.findList();
	}
	
	public static void addSeance(Seance se){
		se.save();
	}
	
	/**
	 * Suppression en cascade nécessaire.
	 * @param id
	 */
	public static void removeSeance(Long id){
		Seance se = Seance.find.byId(id);
		if(se != null){
			List<Serie> ss = Serie.find.where().eq("seance",se).findList();
			for(Serie s: ss){
				Serie.removeSerie(s.id);
			}
			se.delete();
		}
	}
	
	/**
	 * Trouve une id non utilisée. Cela permet de rajouter un séance en choisissant son ID
	 * plutôt que de laisser faire ebean. Cela peut parfois être utile.
	 * @return ID non utilisée.
	 */
	public static Long idNonUtilisee(){
		List<Seance> seanceTemp = Seance.find.orderBy("id desc").findList();
		if(!seanceTemp.isEmpty()){
			return seanceTemp.get(0).id+1;
		}else{
			return 1L;
		}
	}
	
	/**
	 * Détermine si oui ou non on peut accéder à la page vote et résultats pour une séance donnée.
	 * @return VRAI ou FAUX
	 */
	public boolean canAccessVoteResultat(){
		return !this.series.isEmpty() && !this.date.equals(functions.ParseDate.lastDate());
	}
	
	/**
	 * Vérifie s'il y a au moins une série lançable dans la séance
	 * Utile pour le bouton "Lancer toutes les séries d'un coup".
	 * @return VRAI ou FAUX
	 */
	public boolean IsAllLaunchable(){
		for(Serie serie : series){
			if(serie.launchable()){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Vérifie si au moins une série a été terminée dans la séance
	 * Utile pour le bouton "Télécharger le bilan".
	 * @return VRAI ou FAUX
	 */
	public boolean hasAnySerieFinished(){
		boolean result=false;
		int i = 0;
		while(!result && i<series.size()){
			result=!series.get(i).isNotFinished();
			i++;
		}
		return result;
	}
	
	/**
	 * Vérifie si au moins une série est en cours
	 * @return VRAI ou FAUX
	 */
	public boolean hasAnySerieBegun(){
		boolean result=false;
		int i = 0;
		while(!result && i<series.size()){
			result=(series.get(i).date_ouverte!=null)&&(series.get(i).isNotFinished());
			i++;
		}
		return result;
	}
	
}