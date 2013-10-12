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

import functions.LDAP;
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
		session().clear();
		if(identifiant.equals(play.Play.application().configuration().getString("admin.login"))
			&& passw.equals(play.Play.application().configuration().getString("admin.pass"))){
			session("admin","logged");
			return admin.Display.main();
		}
		if(user.check(identifiant, passw)){
			session("username",identifiant);
			return profSeancesListe("");
		}else{
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
