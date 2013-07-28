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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.Constraints.*;


/**
 * Contient les séances
 * @author Admin
 *
 */
@Entity
public class Seance extends Model {
	@Id
	public Long id;
	
	@Required
	public Date date;
	@Required
	public String matiere;
	@Required
	public String intitule;
	
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
		try{
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			date = df.parse("2038/01/19 03:14:08");
		} catch(ParseException e){
		}
		
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
	 * Définir le finder pour cette table
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
	
}