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

import play.mvc.Controller;
import play.mvc.Result;
import views.html.error404;


/**
 * Pour faire une page 404, mais aussi pour rediriger les professeurs si jamais ils font des
 * requêtes GET qui n'aboutiraient normalement pas.
 * @author Admin
 *
 */
public class P404 extends Controller{
	
	/**
	 * Affiche la page 404, sauf si un professeur est déjà connecté. Dans ce cas,
	 * il est redirigé vers la page de gestion des séances.
	 * On peut envisager de rediriger vers plusieurs pages possibles en fonction de ce que
	 * le professeur a entré en url (c'est pour l'instant non implémenté).
	 * @param url : l'url que le prof a rentré.
	 * @return affiche la page 404 ou bien redirige vers la liste des séances
	 */
	public static Result p404(String url) {
		if(session("username")==null){
	        return ok(error404.render());
		}else{
			return versBonnePage(url);
		}
    }
	
	/**
	 * Redirige soit vers la page avec la liste de séance, la préparation du vote
	 * ou la gestion d'une séance en fonction de l'url entrée et des informations stockées
	 * dans les cookies.
	 * @param url
	 * @return une des 3 pages citées précédemment
	 */
	public static Result versBonnePage(String url){
		System.out.println(url);
		if(url.startsWith("prof/gerer") || url.startsWith("prof/export") || url.startsWith("prof/upload") ){
			if(session("seance")==null){
				return redirect(routes.Login.profSeancesListe(""));
			}else{
				return redirect(routes.SeancesListe.gererSeance( Long.parseLong(session("seance")) ));
			}
		}
		if(url.startsWith("prof/vote") || url.startsWith("prof/lancer") || url.startsWith("prof/fin") || url.startsWith("prof/reset") || url.startsWith("prof/download")){
			if(session("vote")==null){
				return redirect(routes.Login.profSeancesListe(""));
			}else{
				return redirect(routes.Application.voteSeance( Long.parseLong(session("vote")) ));
			}
		}
		if(url.startsWith("tuto")){
			return redirect(routes.Tuto.tutorial());
		}
		return redirect(routes.Login.profSeancesListe(""));
	}
}