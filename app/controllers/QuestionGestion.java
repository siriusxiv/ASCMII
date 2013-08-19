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

import java.util.Collections;
import java.util.List;

import models.Question;
import models.Reponse;
import models.Serie;
import models.TypeQuestion;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import views.html.editQuestion;
import views.html.nouvelleQuestion;
import views.html.nouvelleQuestion2;

public class QuestionGestion extends Controller {

	/**
	 * Affiche la page donnant le choix du type de question
	 * @param serie_id : id de la série à laquelle appartiendra la future question
	 * @return
	 */
	public static Result addQuestion(Long serie_id){
		return ok(nouvelleQuestion.render(serie_id,Serie.find.ref(serie_id).seance.id,TypeQuestion.find.all()));
	}
	/**
	 * Affiche la deuxième page qui permet de créer un question
	 * (celle où on entre toutes les infos à par son type)
	 * @param serie_id : id de la série à laquelle appartiendra la future question
	 * @return
	 */
	public static Result addQuestion2(Long serie_id){
		DynamicForm info = Form.form().bindFromRequest();
		if(info.get("choixQuestion")==null){
			return ok(nouvelleQuestion.render(serie_id,Serie.find.ref(serie_id).seance.id,TypeQuestion.find.all()));
		}
		Long n = Long.parseLong(info.get("choixQuestion"));
		return ok(nouvelleQuestion2.render(serie_id,Serie.find.ref(serie_id).seance.id,TypeQuestion.find.ref(n),""));
	}
	/**
	 * Affiche la deuxième page d'ajout de question. Permet de faire passer un message d'erreur,
	 * comme par exemple : "Vous n'avez pas écrit d'intitulé à la question".
	 * @param serie_id : id de la série à laquelle appartiendra la future question
	 * @param typeQ_id : type de la future question
	 * @param log : un message que l'on veut faire apparaître sur la page
	 * @return
	 */
	public static Result addQuestionLog(Long serie_id, Long typeQ_id, String log){
		return ok(nouvelleQuestion2.render(serie_id,Serie.find.ref(serie_id).seance.id,TypeQuestion.find.ref(typeQ_id),log));
	}
	/**
	 * Ajoute une question dans la base de donnée
	 * @param serie_id : id de la série à laquelle appartiendra la future question
	 * @param typeQ_id : type de la future question
	 * @return
	 */
	public static Result addQuestion3(Long serie_id, Long typeQ_id){
		Serie serie = Serie.find.ref(serie_id);
		DynamicForm info = Form.form().bindFromRequest();
		MultipartFormData body = request().body().asMultipartFormData();
		String titre = info.get("titre");
		String texte = info.get("texte");
		//D'abord, on vérifie toutes les infos rentrées :
		if(titre==null){//Cela arrive quand trop d'images sont là et que le texte est trop gros.
			return addQuestionLog(serie_id,typeQ_id,"Le texte de votre question est trop long. Vous avez certainement mis trop d'images, ou bien l'image que vous avez mise est trop volumineuse. Essayez d'en diminuer la taille avant de l'importer.");
		}
		if(titre.equals("")){
			titre="Titre de la question";
		}
		if(texte.equals("")){
			return addQuestionLog(serie_id,typeQ_id,"Veuillez entrez l'intitulé de la question.");
		}
		//On ajoute la question à la DB :
		Question question = new Question(titre,texte,typeQ_id,serie);
		question.save();
		//On ajoute les réponses à la DB :
		if(typeQ_id<=2){
			int i = 1;
			Question questionQuiAppartientALaReponse = Question.find.ref(question.id);
			while(info.get("reponse"+i)!=null){
				Reponse reponse = new Reponse(info.get("reponse"+i),questionQuiAppartientALaReponse,i);
				reponse.save();
				UploadImages.upload(body.getFile("image"+i),reponse);
				i++;
			}
		}
		return SeancesListe.gererSeance(serie.seance.id);
	}
	/**
	 * Affiche la page qui permet d'éditer une question
	 * @param id : id de la question que l'on édite
	 * @return
	 */
	public static Result editQuestion(Long id){
		Question q = Question.find.ref(id);
		Collections.sort(q.reponses,new Reponse());
		return ok(editQuestion.render(q,""));
	}
	/**
	 * Affiche la page qui permet d'éditer une question avec un petit message d'erreur.
	 * @param id : id de la question que l'on édite
	 * @param log : message d'erreur
	 * @return
	 */
	public static Result editQuestionLog(Long id, String log){//id de la question que l'on édite
		Question q = Question.find.ref(id);
		Collections.sort(q.reponses,new Reponse());
		return ok(editQuestion.render(q, log));
	}
	/**
	 * Enregistre les modifications de la question dans la base de donnée.
	 * @param id : id de la question que l'on édite
	 * @return
	 */
	public static Result editQuestion2(Long id){//id de la question que l'on édite
		Question question = Question.find.ref(id);
		DynamicForm info = Form.form().bindFromRequest();
		String titre = info.get("titre");
		String texte = info.get("texte");
		//D'abord, on vérifie toutes les infos rentrées :
		if(titre==null){//Cela arrive quand trop d'images sont là et que le texte est trop gros. Dans ce cas, la forme renvoie null partour.
			return editQuestionLog(id,"Le texte de votre question est trop long. Vous avez certainement mis trop d'images, ou bien l'image que vous avez mise est trop volumineuse. Essayez d'en diminuer la taille avant de l'importer.");
		}
		if(titre.equals("")){
			return editQuestionLog(id,"Veuillez entrez un titre.");
		}
		if(texte.equals("")){
			return editQuestionLog(id,"Veuillez entrez l'intitulé de la question.");
		}
		if(question.typeQ.id<=2){
			if(info.get("reponse1").equals("") || info.get("reponse2").equals("")){
				return editQuestionLog(id,"Vous devez entrer au moins deux réponses.");
			}
		}
		//On edite le titre et le texte :
		question.titre=titre;
		question.texte=texte;
		question.save();
		//On ajoute les réponses à la DB tout en prenant soin de déplacer automatiquement les images si
		//le professeur a changé l'ordre des réponses :
		if(question.typeQ.id<=2){
			Question questionQuiAppartientALaReponse = Question.find.ref(question.id);
			List<Reponse> oldAnswers = Reponse.find.where().eq("question",questionQuiAppartientALaReponse).orderBy("position").findList();
			List<Reponse> newAnswers = Reponse.reponsesForm(info,questionQuiAppartientALaReponse);
			for(int i = 0; i<newAnswers.size(); i++){
				Reponse r = newAnswers.get(i);
				System.out.println(r.toString());
				int pos;
				if((pos=Reponse.isItIn(oldAnswers,r))>=0){
					//on ajoute l'image
					r.image=oldAnswers.get(pos).image;
					System.out.println("POS"+pos);
				}
				r.save();
			}
			//On supprime les anciennes réponses de la base de donnée
			for(int i = 0; i<oldAnswers.size();i++){
				Reponse.removeReponse(oldAnswers.get(i).id);
			}
		}
		return SeancesListe.gererSeance(question.serie.seance.id);
	}
}
