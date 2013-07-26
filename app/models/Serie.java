/****************************************************************************

Copyright (c) 2013, Boussejra Malik Olivier from the Ecole Centrale de Nantes
All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
* Neither the name of the copyright holder nor the names of its contributors
  may be used to endorse or promote products derived from this software
  without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

******************************************************************************/

package models;
import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.data.validation.Constraints.*;

/**
 * Contient les séries
 * @author Admin
 *
 */
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