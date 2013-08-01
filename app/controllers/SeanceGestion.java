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

package controllers;

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
		Serie serie = new Serie(nom,seance);
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
