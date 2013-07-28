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
				return redirect(routes.Application.gererSeance( Long.parseLong(session("seance")) ));
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