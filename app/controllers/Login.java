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

import models.Seance;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.login;
import views.html.seancesListe;

public class Login extends Controller{
    
    /**
     * Affiche la page de login sans aucun message, si l'utilisateur est déjà connecté,
     * alors on passe directement à la liste des séances.
     * @return
     */
    public static Result profLogin() {
    	if(session("username")!=null){
    		return redirect(routes.Login.profSeancesListe(""));
    	}else{
    		return ok(login.render("T"));
    	}
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
		return redirect(routes.Login.profLogin());
	}
}
