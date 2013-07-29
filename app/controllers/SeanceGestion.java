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

package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Question;
import models.Seance;
import models.Serie;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class SeanceGestion extends Controller{

	/**
	 * Ajoute une série.
	 * @param id : id de la séance à laquelle appartiendra la future série
	 * @return
	 */
	public static Result addSerie(Long id){
		DynamicForm infos = Form.form().bindFromRequest();
		String nom = infos.get("nom");
		Seance seance = Seance.find.ref(id);
		if(nom.equals("")){
			nom="Série "+(seance.series.size()+1);
		}
		Serie serie = new Serie();
		serie.nom = nom;
		serie.seance = seance;
		serie.questions = new ArrayList<Question>();
		//on trouve la position max et on le met à la fin
		serie.position=Serie.positionMax()+1;
		Serie.addSerie(serie);
		return SeancesListe.gererSeance(id);
	}
	/**
	 * Supprime une série
	 * @param serie_id : id de la série que l'on va supprimer
	 * @return
	 */
	public static Result delSerie(Long serie_id){
		Long seance_id = Serie.find.ref(serie_id).seance.id;
		Serie.removeSerie(serie_id);
		return SeancesListe.gererSeance(seance_id);
	}
	/**
	 * Monte la série d'un cran.
	 * @param id : id de la série à monter
	 * @return
	 */
	public static Result monterSerie(Long id){
		Serie serieDuDessous = Serie.find.ref(id);
		Seance seance = serieDuDessous.seance;
		//D'abord, on trouve la série qui est au dessus
		List<Serie> serieTemp = Serie.find.where()
								.lt("position",serieDuDessous.position)
								.eq("seance",seance)
								.orderBy("position desc").findList();
		if(serieTemp.isEmpty()){ //rien à faire
			return SeancesListe.gererSeance(seance.id);
		}
		Serie serieDuDessus = serieTemp.get(0);
		//Après, on swappe leur position
		Long positionTemp = serieDuDessus.position;
		serieDuDessus.position=serieDuDessous.position;
		serieDuDessous.position=positionTemp;
		//on save dans la database
		serieDuDessus.save();
		serieDuDessous.save();
		return SeancesListe.gererSeance(seance.id);
	}
	/**
	 * Descend la série d'un cran.
	 * @param id : id de la série à descendre
	 * @return
	 */
	public static Result descendreSerie(Long id){//cet id est l'id de la série à monter
		Serie serieDuDessus = Serie.find.ref(id);
		Seance seance = serieDuDessus.seance;
		//D'abord, on trouve la série qui est en dessous
		List<Serie> serieTemp = Serie.find.where()
								.gt("position",serieDuDessus.position)
								.eq("seance",seance)
								.orderBy("position").findList();
		if(serieTemp.isEmpty()){ //rien à faire
			return SeancesListe.gererSeance(seance.id);
		}
		Serie serieDuDessous = serieTemp.get(0);
		//Après, on swappe leur position
		Long positionTemp = serieDuDessus.position;
		serieDuDessus.position=serieDuDessous.position;
		serieDuDessous.position=positionTemp;
		//on save dans la database
		serieDuDessus.save();
		serieDuDessous.save();
		return SeancesListe.gererSeance(seance.id);
	}
	
	

	/**
	 * Supprime la question
	 * @param id : id de la question à supprimer
	 * @return
	 */
	public static Result delQuestion(Long id){
		Long seance_id = Question.find.ref(id).serie.seance.id;
		Question.removeQuestion(id);
		return SeancesListe.gererSeance(seance_id);
	}
	/**
	 * Monte la question d'un cran.
	 * @param id : id de la question à monter
	 * @return
	 */
	public static Result monterQuestion(Long id){
		Question qDuDessous = Question.find.ref(id);
		Serie serie = qDuDessous.serie;
		//D'abord, on trouve la question qui est au dessus
		List<Question> qTemp = Question.find.where()
								.lt("position",qDuDessous.position)
								.eq("serie",serie)
								.orderBy("position desc").findList();
		if(qTemp.isEmpty()){ //rien à faire
			return SeancesListe.gererSeance(serie.seance.id);
		}
		Question qDuDessus = qTemp.get(0);
		//Après, on swappe leur position
		Long positionTemp = qDuDessus.position;
		qDuDessus.position=qDuDessous.position;
		qDuDessous.position=positionTemp;
		//on save dans la database
		qDuDessus.save();
		qDuDessous.save();
		return SeancesListe.gererSeance(serie.seance.id);
	}
	/**
	 * Descend la question d'un cran.
	 * @param id : id de la question à descend
	 * @return
	 */
	public static Result descendreQuestion(Long id){
		Question qDuDessus = Question.find.ref(id);
		Serie serie = qDuDessus.serie;
		//D'abord, on trouve la question qui est au dessus
		List<Question> qTemp = Question.find.where()
								.gt("position",qDuDessus.position)
								.eq("serie",serie)
								.orderBy("position asc").findList();
		if(qTemp.isEmpty()){ //rien à faire
			return SeancesListe.gererSeance(serie.seance.id);
		}
		Question qDuDessous = qTemp.get(0);
		//Après, on swappe leur position
		Long positionTemp = qDuDessus.position;
		qDuDessus.position=qDuDessous.position;
		qDuDessous.position=positionTemp;
		//on save dans la database
		qDuDessus.save();
		qDuDessous.save();
		return SeancesListe.gererSeance(serie.seance.id);
	}
}
