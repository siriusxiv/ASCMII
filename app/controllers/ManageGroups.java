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

import java.util.List;

import models.EleveGroupe;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.manageGroups;

public class ManageGroups extends Controller {
	
	@Security.Authenticated(Secured.class)
	public static Result index(){
		List<EleveGroupe> groupes = EleveGroupe.find.all();
		return ok(manageGroups.render(groupes));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result add(){
		DynamicForm form = DynamicForm.form().bindFromRequest();
		EleveGroupe groupe = new EleveGroupe(form.get("nouveauGroupe"));
		groupe.save();
		return ok(groupe.toString());
	}
	
	@Security.Authenticated(Secured.class)
	public static Result del(Long groupe_id){
		EleveGroupe.find.byId(groupe_id).delete();
		return ok("Groupe "+groupe_id+" supprimé");
	}
}
