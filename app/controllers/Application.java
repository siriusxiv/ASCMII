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

import com.typesafe.plugin.*;

public class Application extends Controller {
  
    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
    
    public static Result profLogin() {
    	return ok(login.render("T"));
    }
    
    @Security.Authenticated(Secured.class)
    public static Result profSeancesListe(String log) {
    	return ok(seancesListe.render(Seance.page(session()),log));
    }
    
	public static Result profAuthenticate()
	{
		DynamicForm fullInfos = Form.form().bindFromRequest();
		String identifiant = fullInfos.get("login");
		if(Professeur.find.where().eq("username",identifiant).findList().isEmpty()){
			session().clear();
			return badRequest(login.render("F"));
		}else{
			session().clear();
			session("username",identifiant);
			return profSeancesListe("");
		}
	}
	public static Result logOut(){
		session().clear();
		return redirect(routes.Application.profLogin());
	}
	
	//Gestion des séances
	public static Result addSeance(){
		DynamicForm fullInfos = Form.form().bindFromRequest();
		String day = fullInfos.get("day");
		String month = fullInfos.get("month");
		String year = fullInfos.get("year");
		String hour = fullInfos.get("hour");
		
		Form<Seance> seanceForm = Form.form(Seance.class).bindFromRequest();
		if(fullInfos.get("matiere")!=""){
			Seance newSeance = new Seance();
			if(fullInfos.get("intitule")==""){
				newSeance.intitule="Intitulé";
			}else{
				newSeance.intitule=fullInfos.get("intitule");
			}
			newSeance.matiere=fullInfos.get("matiere");
			newSeance.professeur=Professeur.find.ref(session("username"));
			try{
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			newSeance.date = df.parse(year + "/" + month + "/" + day + " " + hour +":00:00");
			} catch(ParseException e){
				e.printStackTrace();
			}
			Seance.addSeance(newSeance);
			return redirect(routes.Application.profSeancesListe("Séance ajoutée avec succès."));
		}
		return redirect(routes.Application.profSeancesListe("Cette matière n'existe pas."));
	}
	public static Result removeSeance(Long id){
		Seance.removeSeance(id);
		return redirect(routes.Application.profSeancesListe("Séance supprimée."));
	}
	public static Result displayEditSeance(Long id){
		return ok(editSeance.render(Seance.find.ref(id)));
	}
	public static Result editSeance(Long id){
		DynamicForm fullInfos = Form.form().bindFromRequest();
		String day = fullInfos.get("day");
		String month = fullInfos.get("month");
		String year = fullInfos.get("year");
		String hour = fullInfos.get("hour");
		
		Form<Seance> seanceForm = Form.form(Seance.class).bindFromRequest();
		if(fullInfos.get("matiere")!=""){
			removeSeance(id);
			
			Seance newSeance = new Seance();
			newSeance.id=id;
			if(fullInfos.get("intitule")==""){
				newSeance.intitule="Intitulé";
			}else{
				newSeance.intitule=fullInfos.get("intitule");
			}
			newSeance.matiere=fullInfos.get("matiere");
			newSeance.professeur=Professeur.find.ref(session("username"));
			try{
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			newSeance.date = df.parse(year + "/" + month + "/" + day + " " + hour +":00:00");
			} catch(ParseException e){
				e.printStackTrace();
			}
			Seance.addSeance(newSeance);
			return redirect(routes.Application.profSeancesListe("Séance éditée."));
		}
		return redirect(routes.Application.profSeancesListe("Erreur dans l'édition de la séance, cette matière n'existe pas."));
	}
	public static Result dupliquerSeance(Long id){ //il faudra l'éditer pour prendre en compte les séries, questions, etc.
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
		Seance.addSeance(newSeance);
		//suite à écrire...
		return redirect(routes.Application.profSeancesListe("Séance dupliqué avec succès. N'oubliez pas de changer la date de la nouvelle séance. La séance dupliquée se situe en première position dans la liste."));
	}
	public static Result gererSeance(Long id){
		session("seance_id",String.valueOf(id));
		return ok(gerer.render(
					Seance.find.ref(id),
					Serie.page(id)
					));
	}
	public static Result voteSeance(Long id){
		session("seance_id",String.valueOf(id));
		return ok(voteEtResultat.render(id));
	}

	//Gestion des séries
	public static Result addSerie(Long id){
		DynamicForm infos = Form.form().bindFromRequest();
		String nom = infos.get("nom");
		if(nom==""){
			nom="Série sans nom";
		}
		Serie serie = new Serie();
		serie.nom = nom;
		serie.seance = Seance.find.ref(id);
		serie.ouverte = 0; //la série est fermée au départ (0=FAUX, 1=VRAI)
		serie.questions = new ArrayList<Question>();
		//on trouve la position max et on le met à la fin
		List<Serie> serieTemp = Serie.find.orderBy("position desc").findList();
		if(!serieTemp.isEmpty()){
			serie.position = serieTemp.get(0).position+1;
		}else{
			serie.position=0L;
		}
		Serie.addSerie(serie);
		return gererSeance(id);
	}
	public static Result delSerie(Long serie_id){
		Long seance_id = Serie.find.ref(serie_id).seance.id;
		Serie.removeSerie(serie_id);
		return gererSeance(seance_id);
	}
	public static Result monterSerie(Long id){//cet id est l'id de la série à monter
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
	public static Result addQuestion(Long id){ //c'est l'id de la série à laquelle appartien la future question
		return ok(nouvelleQuestion.render(id,TypeQuestion.find.all()));
	}
	public static Result addQuestion2(Long id){ //c'est l'id de la série à laquelle appartien la future question
		DynamicForm info = Form.form().bindFromRequest();
		if(info.get("id")==null){
			return ok(nouvelleQuestion.render(id,TypeQuestion.find.all()));
		}
		Long n = Long.parseLong(info.get("id"));
		return ok(nouvelleQuestion2.render(id,TypeQuestion.find.ref(n)));
	}
	
	//Envoyer les mails
	public static Result sendMail(){
		MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
		mail.setSubject("[ASCMII] Test");
		mail.addRecipient("Malik <malik.boussejra@eleves.ec-nantes.fr>","malik.boussejra@eleves.ec-nantes.fr");
		mail.addFrom("ASCMII <ascmii.test@gmail.com>");
		mail.sendHtml("<html>Ceci est un mail de<br>test.</html>");
		return voteSeance(Long.parseLong(session("seance_id")));
	}
}
