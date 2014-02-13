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
import models.EleveHasGroupe;
import models.Seance;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.manageGroups;
import views.html.selectCustomGroup;

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
		EleveGroupe.find.byId(groupe_id).remove();
		return ok("Groupe "+groupe_id+" supprimé");
	}
	
	@Security.Authenticated(Secured.class)
	public static Result getMembers(Long groupe_id){
		List<EleveHasGroupe> ehgs = EleveHasGroupe.find.where().eq("groupe.id",groupe_id).findList();
		String res = "";
		for(EleveHasGroupe ehg : ehgs){
			res+="<button onClick=\"delEleve("+groupe_id+",'"+ehg.eleve.uid+"')\" class=\"suppr\">X</button>"+ehg.eleve.nom+", "+ehg.eleve.prenom;
		}
		return ok(res);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result addEleveInGroupe(Long groupe_id, String eleve_uid){
		if(EleveHasGroupe.find.where().eq("groupe.id",groupe_id).eq("eleve.uid", eleve_uid).findList().isEmpty()){
			new EleveHasGroupe(groupe_id,eleve_uid).save();
			return ok("Elève "+eleve_uid+" ajouté dans le groupe "+groupe_id);
		}else{
			return ok("L'élève est déjà présent dans ce groupe.");
		}
	}
	
	@Security.Authenticated(Secured.class)
	public static Result delEleveInGroupe(Long groupe_id, String eleve_uid){
		EleveHasGroupe.find.where().eq("groupe.id",groupe_id).eq("eleve.uid", eleve_uid).findUnique().delete();
		return ok("Elève "+eleve_uid+" supprimé du le groupe "+groupe_id);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result getGroupList(Long seance_id){
		Seance seance = Seance.find.byId(seance_id);
		if(seance!=null)
			return ok(selectCustomGroup.render(models.EleveGroupe.find.all(),seance));
		else
			return ok(selectCustomGroup.render(models.EleveGroupe.find.all(),new Seance()));
	}
}
