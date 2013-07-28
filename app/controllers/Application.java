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

import java.util.Calendar;
import java.util.List;

import models.Choisit;
import models.Lien;
import models.Question;
import models.Repond;
import models.Reponse;
import models.Resultat;
import models.Seance;
import models.Serie;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.voteEtResultat;
import views.html.resultatEnCours;
import views.html.resultatFin;
import views.html.reponsesExhaustives;


/**
 * Cette classe contient l'index et toutes les fonctions relatives
 * à la page "Vote et résultats".
 * @author Admin
 *
 */
public class Application extends Controller {
  
	/**
	 * La page d'index renvoie sur le login du professeur
	 * @return
	 */
    public static Result index() {
        return redirect(routes.Login.profLogin());
    }
	
	
	/**
	 * Affiche la page permettant de lancer les votes pour une séance donnée.
	 * On insère aussi l'id de la séance dans la session.
	 * On vérifie que l'utilisateur est authentifié et que c'est lui qui a le droit de modifier cette séance.
	 * Si l'utilisateur vient là avec une requête GET, on le redirige au bon endroit après avoir
	 * vérifié l'existence de la séance qu'il a demandé d'afficher.
	 * @param id : id de la séance pour laquelle le vote va avoir lieu
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public static Result voteSeance(Long id){
		Seance seance = Seance.find.byId(id);
		if(seance!=null && seance.professeur.username.equals(session("username"))){
			session("vote",id.toString());
			return ok(voteEtResultat.render(// page trie automatiquement les réponses et les questions selon leur position
				seance,
				Serie.page(id)
				));
		}else{
			return P404.p404("prof/vote");
		}
	}
	
	
	//Vote et Résulats
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
	
	

}
