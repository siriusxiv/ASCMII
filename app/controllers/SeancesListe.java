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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.Professeur;
import models.Question;
import models.Reponse;
import models.Seance;
import models.Serie;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.editSeance;
import views.html.gerer;

/**
 * Fonctions qui gèrent la liste des séances
 * @author malik
 *
 */
public class SeancesListe extends Controller{
	
	/**
	 * Permet l'ajout d'une séance. On vérifie déjà les informations entrées.
	 * @return
	 */
	public static Result addSeance(){
		DynamicForm info = Form.form().bindFromRequest();
		String day = info.get("day");
		String month = info.get("month");
		String year = info.get("year");
		String hour = info.get("hour");
		
		if(info.get("matiere")!=""){
			Seance newSeance = new Seance();
			if(info.get("intitule")==""){
				newSeance.intitule="Intitulé";
			}else{
				newSeance.intitule=info.get("intitule");
			}
			newSeance.matiere=info.get("matiere");
			newSeance.professeur=Professeur.find.ref(session("username"));
			Date date = null;
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			try{
				date = df.parse(year + "/" + month + "/" + day + " " + hour +":00:00");
			} catch(ParseException e){
			}
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MINUTE, 15);
			if(now.getTime().before(date)){//C'est bon, on est à l'heure.
				System.out.println("À l'heure");
				newSeance.date=date;
				newSeance.save();
				return redirect(routes.Login.profSeancesListe("Séance ajoutée avec succès."));
			}else{
				try {
					newSeance.date=df.parse("2038/01/19 03:14:08");
				} catch (ParseException e) {
				}
				newSeance.save();
				return redirect(routes.Login.profSeancesListe("La date que vous avez choisie se situe dans le passé, veuillez la corriger en cliquant sur le bouton \"Editer\"."));
			}
		}
		return redirect(routes.Login.profSeancesListe("Cette matière n'existe pas."));
	}
	/**
	 * Supprime une séance
	 * @param id : id de la séance à supprimer.
	 * @return
	 */
	public static Result removeSeance(Long id){
		Seance.removeSeance(id);
		return redirect(routes.Login.profSeancesListe("Séance supprimée."));
	}
	/**
	 * Affiche la page qui permet l'édition des infos d'une séance.
	 * @param id : id de la séance à éditer.
	 * @return
	 */
	public static Result displayEditSeance(Long id){
		return ok(editSeance.render(Seance.find.ref(id)));
	}
	/**
	 * Edite une séance. On commence par vérifier les informations entrées.
	 * On vérifie aussi que la date est bien une date future et pas passée.
	 * @param id : id de la séance à éditer.
	 * @return
	 */
	public static Result editSeance(Long id){
		DynamicForm fullInfos = Form.form().bindFromRequest();
		String day = fullInfos.get("day");
		String month = fullInfos.get("month");
		String year = fullInfos.get("year");
		String hour = fullInfos.get("hour");
		
		if(fullInfos.get("matiere")!=""){
			Seance seance = Seance.find.ref(id);
			seance.id=id;
			if(fullInfos.get("intitule")==""){
				seance.intitule="Intitulé";
			}else{
				seance.intitule=fullInfos.get("intitule");
			}
			seance.matiere=fullInfos.get("matiere");
			seance.professeur=Professeur.find.ref(session("username"));
			Date date = null;
			try{
				DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				date = df.parse(year + "/" + month + "/" + day + " " + hour +":00:00");
			} catch(ParseException e){
			}
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MINUTE, 15);
			if(now.getTime().before(date)){//C'est bon, on est à l'heure.
				System.out.println("À l'heure");
				seance.date=date;
				seance.save();
				return redirect(routes.Login.profSeancesListe("Séance éditée."));
			}else{
				seance.save();
				return redirect(routes.Login.profSeancesListe("Le changement de date n'a pas été enregistré, en effet, vous avez spécifié une date qui se situe dans le passé."));
			}
			
		}
		return redirect(routes.Login.profSeancesListe("Erreur dans l'édition de la séance, cette matière n'existe pas."));
	}
	/**
	 * Permet la duplication d'une séance.
	 * Il faut dupliquer aussi toutes les séries, questions et réponses dépendant de cette séance.
	 * @param id : id de la séance à dupliquer.
	 * @return
	 */
	public static Result dupliquerSeance(Long id){
		Seance seanceADupliquer = Seance.find.ref(id);
		Seance newSeance = new Seance();
		newSeance.intitule=seanceADupliquer.intitule;
		newSeance.matiere=seanceADupliquer.matiere;
		newSeance.professeur=seanceADupliquer.professeur;
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try{
			newSeance.date=df.parse("2038/01/19 03:14:08");
		} catch(ParseException e){
		}
		newSeance.series = new ArrayList<Serie>();
		//On choisit l'ID de la prochaine nouvelle Séance
		newSeance.id=Seance.idNonUtilisee();
		newSeance.save();
		//On rajoute les séries :
		List<Serie> series = Serie.find.where().eq("seance",seanceADupliquer).findList();
		for(Serie s : series){
				Serie newSerie = new Serie();
				newSerie.position=s.position;
				newSerie.nom=s.nom;
				newSerie.questions=new ArrayList<Question>();
				newSerie.seance=Seance.find.ref(newSeance.id);
				newSerie.id=Serie.idNonUtilisee();
				newSerie.save();
				for(Question q : s.questions){
					Question newQuestion = new Question();
					newQuestion.reponses=new ArrayList<Reponse>();
					newQuestion.texte=q.texte;
					newQuestion.titre=q.titre;
					newQuestion.typeQ=q.typeQ;
					newQuestion.serie=Serie.find.ref(newSerie.id);
					newQuestion.position=q.position;
					newQuestion.id=Question.idNonUtilisee();
					newQuestion.save();
					for(Reponse r : q.reponses){
						Reponse newReponse = new Reponse();
						newReponse.texte=r.texte;
						newReponse.question=Question.find.ref(newQuestion.id);
						newReponse.position=r.position;
						newReponse.image=r.image;
						newReponse.save();
					}
				}
		}
		return redirect(routes.Login.profSeancesListe("Séance dupliqué avec succès. N'oubliez pas de changer la date de la nouvelle séance en cliquant sur le bouton \"Editer\". La séance dupliquée se situe en première position dans la liste."));
	}
	/**
	 * Affiche la page qui permet la gestion d'une séance (ajout de questions et de séries)
	 * On insère aussi l'id de la séance dans la session.
	 * On vérifie que l'utilisateur est authentifié et que c'est lui qui a le droit de modifier cette séance.
	 * Si l'utilisateur vient là avec une requête GET, on le redirige au bon endroit après avoir
	 * vérifié l'existence de la séance qu'il a demandé d'afficher.
	 * @param id : id de la séance que l'on va gérer
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public static Result gererSeance(Long id){
		Seance seance = Seance.find.byId(id);
		if(seance!=null && seance.professeur.username.equals(session("username"))){
			session("seance",id.toString());
			return ok(gerer.render(// page trie automatiquement les réponses et les questions selon leur position
					seance,
					Serie.page(id),""
					));
		}else{
			return P404.p404("prof/gerer");
		}
	}
	/**
	 * Affiche un message sur la page de gestion des séances.
	 * Cette méthode n'est appelée que par UploadImage.upload pour pouvoir prévenir
	 * comment s'est déroulé l'upload de l'image.
	 * @param id : id de la séance que l'on va gérer
	 * @param message : message à afficher
	 * @return Affiche la page de gestion des séances
	 */
	public static Result gererSeanceLog(Long id, String message){
		Seance seance = Seance.find.byId(id);
		return ok(gerer.render(
				seance,
				Serie.page(id),message
				));
	}
}
