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
import java.util.List;

import functions.Numbers;

import models.Choisit;
import models.Lien;
import models.Question;
import models.Repond;
import models.Reponse;
import models.Resultat;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.eleve;
import views.html.eleveRepondu;
import views.html.p404;
import views.html.serieNonCommencee;
import views.html.tropTard;

public class Eleves extends Controller {

	/**
	 * Affiche la page où l'élève peut répondre à la question.
	 * @param chemin : lien unique qui lie l'élève à la série de question
	 * @return
	 */
	public static Result eleveRepondre(String chemin){
		Lien lien = Lien.find.byId(chemin);
		if(lien!=null){
			//On trie les questions et les réponses
			Collections.sort(lien.serie.questions, new Question());
			for(Question q : lien.serie.questions){
				Collections.sort(q.reponses,new Reponse());
			}
			if(lien.aLHeure() && !lien.repondu){
				return ok(eleve.render(lien,""));//L'élève n'a pas répondu et il est dans les temps : il répondu donc aux questions
			}else{
				if(lien.serie.date_ouverte==null){
					return(ok(serieNonCommencee.render(chemin)));//La série n'a pas commencée !
				}else{
					if(lien.repondu){//L'élève à déjà répondu : on affiche ses résultats
						return ok(eleveRepondu.render(Resultat.listeResultat(lien)));
					}else{//L'élève n'a jamais répondu à la question, et il est trop tard.
						return ok(tropTard.render(lien));
					}
				}
			}
			
		}else{
			return ok(p404.render());
		}
	}
	/**
	 * Affiche la page où l'élève peut répondre à la question avec un petit message qui s'affiche sur la page.
	 * @param chemin : lien unique qui lie l'élève à la série de question
	 * @param log : message d'erreur
	 * @return
	 */
	public static Result eleveRepondreLog(String chemin,String message){
		Lien lien = Lien.find.byId(chemin);
		if(lien!=null){
			//On trie les questions et les réponses
			Collections.sort(lien.serie.questions, new Question());
			for(Question q : lien.serie.questions){
				Collections.sort(q.reponses,new Reponse());
			}
			if(lien.aLHeure() && !lien.repondu){
				return ok(eleve.render(lien,message));//L'élève n'a pas répondu et il est dans les temps : il répondu donc aux questions
			}else{
				if(lien.serie.date_ouverte==null){
					return(ok(serieNonCommencee.render(chemin)));//La série n'a pas commencée !
				}else{
					if(lien.repondu){//L'élève à déjà répondu : on affiche ses résultats
						return ok(eleveRepondu.render(Resultat.listeResultat(lien)));
					}else{//L'élève n'a jamais répondu à la question, et il est trop tard.
						return ok(tropTard.render(lien));
					}
				}
			}
		}else{
			return ok(p404.render());
		}
	}
	/**
	 * Enregistre dans la base de donnée la réponse de l'élève. On vérifie au préalable que toutes les 
	 * informations qu'il a entré sont valides.
	 * @param chemin : lien unique qui lie l'élève à la série de question
	 * @return
	 */
	public static Result donnerReponse(String chemin){
		DynamicForm info = Form.form().bindFromRequest();
		Lien lien = Lien.find.ref(chemin);
		if(lien.repondu){
			return eleveRepondre(chemin);
		}
		//D'abord, il faut vérifier que toutes les réponses sont valides :
		for(Question qu : lien.serie.questions){
			switch(qu.typeQ.id){
			case 1:
				if(info.get("choixReponse"+qu.id)==null){
					return eleveRepondreLog(lien.chemin,"Vous n'avez pas sélectionné de réponse pour la question Radio");
				}
				break;
			case 2:
				boolean bool=false;
				for(Reponse r : qu.reponses){
					if(bool = info.get("choixReponse"+qu.id+"."+r.position)!=null){
						break;
					}
				}
				if(!bool){
					return eleveRepondreLog(lien.chemin,"Vous n'avez pas sélectionné de réponse pour la question CheckBox");
				}
				break;
			case 3:
				if(info.get("texteRepondu"+qu.id)==""){
					return eleveRepondreLog(lien.chemin,"Vous n'avez rien écrit !");
				}
				break;
			case 4:
				String nombre = info.get("nombreRepondu"+qu.id);
				if(nombre=="" || !Numbers.isDouble(nombre)){
					return eleveRepondreLog(lien.chemin,"Vous n'avez pas écrit un nombre !");
				}
				break;
			default:
				System.out.println("mauvais type de question : " + qu.typeQ.id);
			}
		}
		//Maintenant qu'on a vérifié les réponses, on les met dans la base de donnée
		for(Question q : lien.serie.questions){
			switch(q.typeQ.id){
			case 1:
				Choisit choix = new Choisit();
				choix.date=Calendar.getInstance().getTime();
				choix.eleve=lien.eleve;
				List<Reponse> reponses = q.reponses;
				int position = Integer.parseInt(info.get("choixReponse"+q.id));
				for(Reponse r : reponses){
					if(r.position==position){
						choix.reponse=r;
					}
				}
				choix.save();
				break;
			case 2:
				List<Reponse> reponses2 = q.reponses;
				for(Reponse r : reponses2){
					String str = info.get("choixReponse"+q.id+"."+r.position);
					if(str!=null){
						Choisit choix2 = new Choisit();
						choix2.date=Calendar.getInstance().getTime();
						choix2.eleve=lien.eleve;
						choix2.reponse=r;
						choix2.save();
					}
				}
				break;
			case 3:
				String ceQuIlARepondu = info.get("texteRepondu"+q.id);
				Repond repond = new Repond();
				repond.date=Calendar.getInstance().getTime();
				repond.eleve=lien.eleve;
				repond.question=q;
				repond.texte=ceQuIlARepondu;
				repond.save();
				break;
			case 4:
				String ceQuIlARepondu2 = info.get("nombreRepondu"+q.id);
				Repond repond2 = new Repond();
				repond2.date=Calendar.getInstance().getTime();
				repond2.eleve=lien.eleve;
				repond2.question=q;
				repond2.texte=ceQuIlARepondu2.replace('.', ',');
				repond2.save();
				break;
			default:
				System.out.println("Mauvais type de question : " + q.typeQ.id);
			}
		}
		lien.repondu=true;
		lien.save();
		return eleveRepondre(chemin);
	}
}
