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

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import functions.Events;
import functions.Numbers;

import models.Seance;
import models.Serie;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.demarrageSerie;
import views.html.demarrageToutesSeries;

/**
 * Contient les fonctions qui permettent de lancer les séries de question.
 * @author Admin
 *
 */
public class LancerSerie extends Controller{

	/**
	 * Cette fonction crée les liens qui permettront aux élèves de répondre aux questions.
	 * Elle envoie de plus aux élèves le mail contenant ses liens.
	 * @param id : id de la série pour laquelle on génère les liens
	 * @return
	 */
	public static Result creerLiens(Long id){
		Seance seance = Seance.find.ref(id);
		Events.sendMailAndCreateLinks(seance);
		return Application.voteSeance(id);
	}
	/**
	 * Affiche la page où l'on choisit la manière dont se finira la série juste avant de la lancer.
	 * Cependant, si l'id spécifiée en argument est nulle, alors on lancera toutes les séries qui
	 * sont dans la séance en session actuellement.
	 * @param serie_id : id de la série
	 * @return
	 */
	public static Result lancerSerie(Long serie_id){
		if(serie_id==0){
			String seance = session("vote");
			if(seance==null){
				return Login.profSeancesListe("Vous avez été redirigé ici car vous venez certainement de remettre à 0 vos cookies.");
			}else{
				return lancerToutesLesSeries(Long.parseLong(seance),"");
			}
		}else{
			return ok(demarrageSerie.render(Serie.find.ref(serie_id), ""));
		}
	}
	/**
	 * Affiche la page où l'on choisit la manière dont se finira la série juste avant de la lancer
	 * avec un petit message d'erreur
	 * @param serie_id : id de la série
	 * @param log : message d'erreur
	 * @return
	 */
	public static Result lancerSerieLog(Long serie_id, String log){
		if(serie_id==0){
			String seance = session("vote");
			if(seance==null){
				return Login.profSeancesListe("Vous avez été redirigé ici car vous venez certainement de remettre à 0 vos cookies.");
			}else{
				return lancerToutesLesSeries(Long.parseLong(seance),"La durée spécifiée doit être entière !");
			}
		}else{
			return ok(demarrageSerie.render(Serie.find.ref(serie_id), log));
		}
	}
	/**
	 * Lance la série conformément aux information entrées sur la template demarrageSerie.scala.html.
	 * @param id : id de la série qui va démarrer
	 * @return
	 */
	public static Result lancerSerie2(Long id){
		Serie serie = Serie.find.ref(id);
		if(serie.date_ouverte==null){//Si la série est déjà ouverte, on ne doit rien faire (accessible avec le back du browser)
			Calendar now = Calendar.getInstance();
			serie.date_ouverte=now.getTime();
			DynamicForm info = Form.form().bindFromRequest();
			if(info.get("fin").equals("finAutomatique")){ //l'utilisateur à sélectionné fin automatique
				String duree = info.get("duree");
				if(!Numbers.isInt(duree)){
					return lancerSerieLog(id,"La durée spécifiée doit être entière !");
				}else{
					int dureeSeconde = Integer.parseInt(duree);
					now.add(Calendar.SECOND, dureeSeconde);
					serie.date_fermeture = now.getTime();
				}
			}
			serie.save();
		}
		return redirect(routes.Application.voirResultats(id));
	}
	
	/**
	 * Affiche la page demarrageToutesSeries.scala.html.
	 * @param seance_id
	 * @param log
	 * @return
	 */
	static Result lancerToutesLesSeries(Long seance_id, String log){
		return ok(demarrageToutesSeries.render(Seance.find.ref(seance_id),log));
	}
	
	/**
	 * Lance toutes les séries non lancées de la séance selon les paramètres définis par l'utilisateur.
	 * Renvoie la page des résultats de la première série lancée lors de cette méthode.
	 * @param seance_id
	 * @return la page des résultats de la première série lancée lors de cette méthode.
	 */
	public static Result lancerToutesLesSeries2(Long seance_id){
		Seance seance = Seance.find.ref(seance_id);
		DynamicForm info = Form.form().bindFromRequest();
		Calendar nowCal = Calendar.getInstance();
		Calendar afterCal = (Calendar) nowCal.clone();
		boolean automatique;
		if(automatique = info.get("fin").equals("finAutomatique")){ //l'utilisateur à sélectionné fin automatique
			String duree = info.get("duree");
			if(!Numbers.isInt(duree)){
				return lancerSerieLog(0L,"La durée spécifiée doit être entière !");
			}else{
				int dureeMinute = Integer.parseInt(duree);
				afterCal.add(Calendar.MINUTE, dureeMinute);
			}
		}
		Date now = nowCal.getTime();
		Date after = afterCal.getTime();
		Collections.sort(seance.series,new Serie());
		for(Serie serie : seance.series){
			if(serie.date_ouverte==null){//Si la série est déjà ouverte, on ne doit rien faire (accessible avec le back du browser)
				serie.date_ouverte=now;
				if(automatique){
					serie.date_fermeture = after;
				}
				serie.save();
			}
		}
		return redirect(routes.Application.voteSeance(seance_id));
	}
	
	/**
	 * Met fin à toutes les séries non terminées dans la séance donnée
	 * @param seance_id
	 * @return affiche la page Vote et Résultats
	 */
	static Result fermerToutesLesSeries(Long seance_id){
		Seance seance = Seance.find.ref(seance_id);
		for(Serie s : seance.series){
			s.mettreFin();
		}
		return redirect(routes.Application.voteSeance(seance_id));
	}
	
}
