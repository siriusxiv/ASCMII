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

import play.*;
import play.mvc.*;
import views.html.*;
import models.*;
import play.data.*;

import java.text.*;
import java.util.*;


/**
 * Cette classe contient toutes les méthodes relatives qui sont appelées par l'intermédiaire des routes.
 * @author Admin
 *
 */
public class Application extends Controller {
  
	/**
	 * La page d'index renvoie sur le login du professeur
	 * @return
	 */
    public static Result index() {
        return redirect(routes.Application.profLogin());
    }
    
    /**
     * Affiche la page de login sans aucun message
     * @return
     */
    public static Result profLogin() {
    	return ok(login.render("T"));
    }
    
    /**
     * Affiche la liste des séances à condition que l'utilisateur soit authentifié
     * @param log : un message que l'on fera apparaître dans la page qui montre la liste des séances.
     * @return
     */
    @Security.Authenticated(Secured.class)
    public static Result profSeancesListe(String log) {
    	return ok(seancesListe.render(Seance.page(session()),log));
    }
    /**
     * Vérifie que le professeur est bien authentifié. Si tel est le cas, renvoie la page affichant
     * la liste des séances.
     * @return
     */
	public static Result profAuthenticate()
	{
		DynamicForm info = Form.form().bindFromRequest();
		String identifiant = info.get("login");
		String passw = info.get("passw");
		/*if(Professeur.find.byId(identifiant)==null){
			session().clear();
			return badRequest(login.render("F"));
		}else{
			session().clear();
			session("username",identifiant);
			return profSeancesListe("");
		}*/
		LDAP user = new LDAP();
		if(user.check(identifiant, passw)){
			session().clear();
			session("username",identifiant);
			return profSeancesListe("");
		}else{
			session().clear();
			return badRequest(login.render("F"));
		}
	}
	/**
	 * Pour se déconnecter. On vide d'abord la session.
	 * @return
	 */
	public static Result logOut(){
		session().clear();
		return redirect(routes.Application.profLogin());
	}
	
	
	//Gestion des séances :
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
		
		Form<Seance> seanceForm = Form.form(Seance.class).bindFromRequest();
		if(info.get("matiere")!=""){
			Seance newSeance = new Seance();
			if(info.get("intitule")==""){
				newSeance.intitule="Intitulé";
			}else{
				newSeance.intitule=info.get("intitule");
			}
			newSeance.matiere=info.get("matiere");
			newSeance.professeur=Professeur.find.ref(session("username"));
			try{
				DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				newSeance.date = df.parse(year + "/" + month + "/" + day + " " + hour +":00:00");
			} catch(ParseException e){
				e.printStackTrace();
			}
			newSeance.series = new ArrayList<Serie>();
			Seance.addSeance(newSeance);
			return redirect(routes.Application.profSeancesListe("Séance ajoutée avec succès."));
		}
		return redirect(routes.Application.profSeancesListe("Cette matière n'existe pas."));
	}
	/**
	 * Supprime une séance
	 * @param id : id de la séance à supprimer.
	 * @return
	 */
	public static Result removeSeance(Long id){
		Seance.removeSeance(id);
		return redirect(routes.Application.profSeancesListe("Séance supprimée."));
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
	 * Edite une séance. On commence par vérifier les informations entrées
	 * @param id : id de la séance à éditer.
	 * @return
	 */
	public static Result editSeance(Long id){
		DynamicForm fullInfos = Form.form().bindFromRequest();
		String day = fullInfos.get("day");
		String month = fullInfos.get("month");
		String year = fullInfos.get("year");
		String hour = fullInfos.get("hour");
		
		Form<Seance> seanceForm = Form.form(Seance.class).bindFromRequest();
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
			try{
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			seance.date = df.parse(year + "/" + month + "/" + day + " " + hour +":00:00");
			} catch(ParseException e){
				e.printStackTrace();
			}
			seance.save();
			return redirect(routes.Application.profSeancesListe("Séance éditée."));
		}
		return redirect(routes.Application.profSeancesListe("Erreur dans l'édition de la séance, cette matière n'existe pas."));
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
			newSeance.date=df.parse("2001/01/01 00:00:00");
		} catch(ParseException e){
			e.printStackTrace();
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
						newReponse.save();
					}
				}
		}
		return redirect(routes.Application.profSeancesListe("Séance dupliqué avec succès. N'oubliez pas de changer la date de la nouvelle séance. La séance dupliquée se situe en première position dans la liste."));
	}
	/**
	 * Affiche la page qui permet la gestion d'une séance (ajout de questions et de séries)
	 * @param id : id de la séance que l'on va gérer
	 * @return
	 */
	public static Result gererSeance(Long id){
		return ok(gerer.render(// page trie automatiquement les réponses et les questions selon leur position
					Seance.find.ref(id),
					Serie.page(id)
					));
	}
	/**
	 * Affiche la page permettant de lancer les votes pour une séance donnée.
	 * @param id : id de la séance pour laquelle le vote va avoir lieu
	 * @return
	 */
	public static Result voteSeance(Long id){//id de la séance pour laquelle on votera
		return ok(voteEtResultat.render(// page trie automatiquement les réponses et les questions selon leur position
				Seance.find.ref(id),
				Serie.page(id)
				));
	}

	
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
		return gererSeance(id);
	}
	/**
	 * Supprime une série
	 * @param serie_id : id de la série que l'on va supprimer
	 * @return
	 */
	public static Result delSerie(Long serie_id){
		Long seance_id = Serie.find.ref(serie_id).seance.id;
		Serie.removeSerie(serie_id);
		return gererSeance(seance_id);
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
			return gererSeance(seance.id);
		}
		Serie serieDuDessus = serieTemp.get(0);
		//Après, on swappe leur position
		Long positionTemp = serieDuDessus.position;
		serieDuDessus.position=serieDuDessous.position;
		serieDuDessous.position=positionTemp;
		//on save dans la database
		serieDuDessus.save();
		serieDuDessous.save();
		return gererSeance(seance.id);
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
			return gererSeance(seance.id);
		}
		Serie serieDuDessous = serieTemp.get(0);
		//Après, on swappe leur position
		Long positionTemp = serieDuDessus.position;
		serieDuDessus.position=serieDuDessous.position;
		serieDuDessous.position=positionTemp;
		//on save dans la database
		serieDuDessus.save();
		serieDuDessous.save();
		return gererSeance(seance.id);
	}
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
		if(typeQ_id<=2){
			if(info.get("reponse1").equals("") || info.get("reponse2").equals("")){
				return addQuestionLog(serie_id,typeQ_id,"Vous devez entrer au moins deux réponses.");
			}
		}
		//On ajoute la question à la DB :
		Question question = new Question();
		question.titre=titre;
		question.texte=texte;
		question.position=Question.positionMax()+1;
		question.typeQ=TypeQuestion.find.ref(typeQ_id);
		question.serie=serie;
		question.reponses = new ArrayList<Reponse>();
		question.id=Question.idNonUtilisee();
		question.save();
		//On ajoute les réponses à la DB :
		if(typeQ_id<=2){
			List<Reponse> reponses = new ArrayList<Reponse>();
			int i = 1;
			Question questionQuiAppartientALaReponse = Question.find.ref(question.id);
			while(info.get("reponse"+i)!=null){
				Reponse reponse = new Reponse();
				reponse.texte = info.get("reponse"+i);
				reponse.question=questionQuiAppartientALaReponse;
				reponse.position=i;
				reponse.save();
				i++;
			}
		}
		return gererSeance(serie.seance.id);
	}
	/**
	 * Supprime la question
	 * @param id : id de la question à supprimer
	 * @return
	 */
	public static Result delQuestion(Long id){
		Long seance_id = Question.find.ref(id).serie.seance.id;
		Question.removeQuestion(id);
		return gererSeance(seance_id);
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
			return gererSeance(serie.seance.id);
		}
		Question qDuDessus = qTemp.get(0);
		//Après, on swappe leur position
		Long positionTemp = qDuDessus.position;
		qDuDessus.position=qDuDessous.position;
		qDuDessous.position=positionTemp;
		//on save dans la database
		qDuDessus.save();
		qDuDessous.save();
		return gererSeance(serie.seance.id);
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
			return gererSeance(serie.seance.id);
		}
		Question qDuDessous = qTemp.get(0);
		//Après, on swappe leur position
		Long positionTemp = qDuDessus.position;
		qDuDessus.position=qDuDessous.position;
		qDuDessous.position=positionTemp;
		//on save dans la database
		qDuDessus.save();
		qDuDessous.save();
		return gererSeance(serie.seance.id);
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
		if(titre==null){//Cela arrive quand trop d'images sont là et que le texte est trop gros.
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
		//On ajoute les réponses à la DB :
		if(question.typeQ.id<=2){
			int i = 1;
			Question questionQuiAppartientALaReponse = Question.find.ref(question.id);
			List<Reponse> reponses = Reponse.find.where().eq("question",questionQuiAppartientALaReponse).orderBy("position").findList();
			while(info.get("reponse"+i)!=null){
				if(i<=reponses.size()){
					reponses.get(i-1).texte = info.get("reponse"+i);
					reponses.get(i-1).save();
				}else{
					Reponse reponse = new Reponse();
					reponse.texte = info.get("reponse"+i);
					reponse.question=questionQuiAppartientALaReponse;
					reponse.position=i;
					reponse.save();
				}
				i++;
			}
			while(i<=reponses.size()){
				Reponse.removeReponse(reponses.get(i-1).id);
			}
		}
		return gererSeance(question.serie.seance.id);
	}
	
	
	//Vote et Résulats
	/**
	 * Cette fonction crée les liens qui permettront aux élèves de répondre aux questions.
	 * Elle envoie de plus aux élèves le mail contenant ses liens.
	 * @param id : id de la série pour laquelle on génère les liens
	 * @return
	 */
	public static Result creerLiens(Long id){
		Seance seance = Seance.find.ref(id);
		List<Eleve> eleves = Eleve.find.all();//pour l'instant, on va supposer que tous les élèves doivent répondre à la série de question
		for(Eleve e : eleves){
			for(Serie s : seance.series){
				Lien.addLien(e, s);
			}
			Mail mail = new Mail(e,seance);
			mail.sendMail();//on envoit le mail
		}
		return voteSeance(id);
	}
	/**
	 * Affiche la page où l'on choisit la manière dont se finira la série juste avant de la lancer
	 * @param id : id de la série
	 * @return
	 */
	public static Result lancerSerie(Long id){
		return ok(demarrageSerie.render(Serie.find.ref(id), ""));
	}
	/**
	 * Affiche la page où l'on choisit la manière dont se finira la série juste avant de la lancer
	 * avec un petit message d'erreur
	 * @param id : id de la série
	 * @param log : message d'erreur
	 * @return
	 */
	public static Result lancerSerieLog(Long id, String log){
		return ok(demarrageSerie.render(Serie.find.ref(id), log));
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
				if(!Fonctions.isInt(duree)){
					return lancerSerieLog(id,"La durée spécifiée n'est pas valide !");
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
	 * Affiche les résultats alors que les élèves sont en train de répondre. La page se rafraîchi au fur et à mesure
	 * @param serie_id
	 * @return
	 */
	public static Result resultatEnCours(Long serie_id){
		Serie serie = Serie.find.ref(serie_id);
		return ok(resultatEnCours.render(Resultat.listeResultat(serie),
				serie,
				Lien.find.where().eq("repondu", true).eq("serie",serie).findList().size(),
				Lien.find.where().eq("serie",serie).findList().size()
				));
	}
	/**
	 * Affiche les résultats finaux une fois qu'une série est close.
	 * @param serie_id
	 * @return
	 */
	public static Result resultatFin(Long serie_id){
		Serie serie = Serie.find.ref(serie_id);
		List<Serie> seriesSuivantes = Serie.find.where()
					.eq("seance",serie.seance)
					.isNull("date_ouverte")
					.gt("position", serie.position)
					.orderBy("position").findList();
		Serie serieSuivante;
		if(seriesSuivantes.isEmpty()){
			serieSuivante=null;
		}else{
			serieSuivante=seriesSuivantes.get(0);
		}
		return ok(resultatFin.render(Resultat.listeResultat(serie),
				serie,serieSuivante,
				Lien.find.where().eq("repondu", true).eq("serie",serie).findList().size(),
				Lien.find.where().eq("serie",serie).findList().size()
				));
	}
	/**
	 * Permet à un professeur authentifié de voir les résultats
	 * Si la série est terminée, on verra les résultats finaux, sinon,
	 * on verra les résultats au fur et à mesure.
	 * @param serie_id
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public static Result voirResultats(Long serie_id){
		Serie serie = Serie.find.ref(serie_id);
		if(serie.date_fermeture==null || serie.date_fermeture.after(Calendar.getInstance().getTime())){
			return resultatEnCours(serie_id);
		}else{
			return resultatFin(serie_id);
		}
	}
	/**
	 * Met fin manuellement à une série.
	 * @param serie_id
	 * @return
	 */
	public static Result finirSerie(Long serie_id){
		Serie serie = Serie.find.ref(serie_id);
		serie.date_fermeture=Calendar.getInstance().getTime();
		serie.save();
		return resultatFin(serie_id);
	}
	/**
	 * Remet à zéro une série. Toutes les réponses sont supprimées et la série est considérée comme n'ayant
	 * jamais été ouverte. Les élèves peuvent alors à nouveau y répondre.
	 * @param serie_id
	 * @return
	 */
	public static Result resetSerie(Long serie_id){
		Serie serie = Serie.find.ref(serie_id);
		serie.date_ouverte=null;
		serie.date_fermeture=null;
		serie.save();
		//On supprime toutes les réponses (Repond et Choisit) des élèves pour cette série
		for(Question q: serie.questions){
			//On supprime les réponses texte et nombre
			for(Repond re : q.estRepondue){
				Repond.removeRepond(re.id);
			}
			//On supprime les réponses choisies
			for(Reponse r : q.reponses){
				List<Choisit> list = Choisit.find.where().eq("reponse", r).findList();
				for(Choisit c : list){
					Choisit.removeChoisit(c.id);
				}
			}
		}
		//On change tous les lien.repondu en false
		for(Lien lien : serie.liens){
			lien.repondu=false;
			lien.save();
		}
		return voteSeance(serie.seance.id);
	}
	/**
	 * Affiche exhaustivement les réponses données par les élèves pour une question de type 3 ou 4.
	 * @param question_id
	 * @return
	 */
	public static Result reponsesExhaustives(Long question_id){
		return ok(reponsesExhaustives.render(Resultat.exhaustive(question_id)));
	}
	
	
	//Côté Elève :
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
				if(nombre=="" || !Fonctions.isDouble(nombre)){
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
