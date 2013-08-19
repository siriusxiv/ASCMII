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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;


/**
 * Contient les séries
 * @author Admin
 *
 */
@SuppressWarnings("serial")
@Entity
public class Serie extends Model implements Comparator<Serie>{
	@Id
	public Long id;
	
	@Required
	public String nom;
	
	public Date date_ouverte;
	public Date date_fermeture;
	
	@Required
	public Long position;
	
	@ManyToOne
	public Seance seance;
	
	@OneToMany(targetEntity = Question.class)
	public List<Question> questions;
	@OneToMany(targetEntity = Lien.class)
	public List<Lien> liens;
	
	/**
	 * La relation d'ordre est définie à partir des positions des séries.
	 */
	@Override
	public int compare(Serie s1,Serie s2){
			return (s1.position<s2.position ? -1 : (s1.position==s2.position ? 0 : 1));
	}
	
	/**
	 * Pour rétrocompatibilité
	 */
	public Serie(){}
	
	/**
	 * Copie la série telle quelle en la liant à la séance en argument.
	 * (utilisée dans dupliquerSeance)
	 * @param serie : série à copier
	 * @param _seance
	 */
	public Serie(Serie serie, Seance _seance){
		position=serie.position;
		nom=serie.nom;
		seance=_seance;
		id=Serie.idNonUtilisee();
	}
	
	/**
	 * Pour ajouter des nouvelles questions dans la base de donnée. (Attention, cette
	 * méthode ne fait qu'instancier l'objet, elle ne le stocke pas dans la base ! Vous
	 * devez faire question.save() pour l'y sauvegarder).
	 * @param _nom
	 * @param _seance
	 */
	public Serie(String _nom, Seance _seance){
		nom=_nom;
		seance=_seance;
		//on trouve la position max et on le met à la fin
		position=positionMax()+1;
	}
	
	public static Finder<Long,Serie> find = new Finder<Long,Serie>(Long.class, Serie.class);
	
	/**
	 * Renvoie la liste des séries appartenant à une séance donnée.
	 * Le tout est ensuite trié (questions et réponses).
	 * @param id : id de la séance pour laquelle on désire avoir les séries
	 * @return liste de séries.
	 */
	public static List<Serie> page(Long id){
		Seance seance = Seance.find.ref(id);
		List<Serie> series = find.where().eq("seance",seance).orderBy("position").findList();
		for(Serie s : series){
			Collections.sort(s.questions, new Question());
			for(Question q : s.questions){
				Collections.sort(q.reponses, new Reponse());
			}
		}
		return series;
	}
	
	public static void addSerie(Serie se){
		se.save();
		for(Question q : se.questions){
			q.save();
			for(Reponse r: q.reponses){
				r.save();
			}
		}
	}
	
	/**
	 * Suppression en cascade nécessaire.
	 * @param id
	 */
	public static void removeSerie(Long id){
		Serie se = Serie.find.byId(id);
		if(se != null){
			List<Question> qs = Question.find.where().eq("serie",se).findList();
			for(Question q : qs){
				Question.removeQuestion(q.id);
			}
			List<Lien> ls = Lien.find.where().eq("serie",se).findList();
			for(Lien l : ls){
				Lien.removeLien(l.chemin);
			}
			se.delete();
		}
	}
	
	/**
	 * Met fin à la série
	 */
	public void mettreFin(){
		date_fermeture=Calendar.getInstance().getTime();
		save();
	}
	
	/**
	 * Renvoie la position maximale parmi la position de toutes les séries.
	 * S'il n'y a aucune série dans la base de donnée, renvoie -1.
	 * @return
	 */
	public static Long positionMax(){
		List<Serie> serieTemp = Serie.find.orderBy("position desc").findList();
		if(!serieTemp.isEmpty()){
			return serieTemp.get(0).position;
		}else{
			return -1L;
		}
	}
	
	/**
	 * Trouve une id non utilisée. Cela permet de rajouter un série en choisissant son ID
	 * plutôt que de laisser faire ebean. Cela peut parfois être utile.
	 * @return ID non utilisée.
	 */
	public static Long idNonUtilisee(){
		List<Serie> serieTemp = Serie.find.orderBy("id desc").findList();
		if(!serieTemp.isEmpty()){
			return serieTemp.get(0).id+1;
		}else{
			return 1L;
		}
	}
	
	/**
	 * Renvoie si oui ou non la série est lançable.
	 * @return VRAI ou FAUX
	 */
	public boolean launchable(){
		return !liens.isEmpty() && !questions.isEmpty() && date_ouverte==null;
	}
	
	/**
	 * Renvoie si oui ou non la série est terminée.
	 * @return VRAI ou FAUX
	 */
	public boolean isNotFinished(){
		return date_fermeture==null || date_fermeture.after(Calendar.getInstance().getTime());
	}
}