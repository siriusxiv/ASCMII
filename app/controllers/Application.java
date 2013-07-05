package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import models.*;
import play.data.*;

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
		Form<Prof> profForm = Form.form(Prof.class).bindFromRequest();
		if(profForm.hasErrors()){
			session().clear();
			return badRequest(login.render("F"));
		}else{
			session().clear();
			session("username",profForm.get().login);
			return profSeancesListe("");
		}
	}
	
	
	//Gestion des séances
	public static Result addSeance(){
		Form<Seance> seanceForm = Form.form(Seance.class).bindFromRequest();
		if(!seanceForm.hasErrors()){
			Seance se = seanceForm.get();
			se.professeur=Professeur.find.ref(session("username"));
			Seance.addSeance(se);
			return profSeancesListe("Séance ajoutée.");
		}
		return profSeancesListe("Erreur, veuillez vérifier ce que vous avez écrit.");
	}
	public static Result removeSeance(Long id){
		Seance.removeSeance(id);
		return redirect(routes.Application.profSeancesListe("Séance supprimée."));
	}
	public static Result displayEditSeance(Long id){
		return ok(editSeance.render(Seance.find.ref(id)));
	}
	public static Result editSeance(Long id){
		Form<Seance> seanceForm = Form.form(Seance.class).bindFromRequest();
		if(!seanceForm.hasErrors()){
			removeSeance(id);
			Seance newSeance = seanceForm.get();
			newSeance.id=id;
			newSeance.professeur=Professeur.find.ref(session("username"));
			Seance.addSeance(newSeance);
			return redirect(routes.Application.profSeancesListe("Séance éditée."));
		}
		return redirect(routes.Application.profSeancesListe("Erreur dans l'édition de la séance, vérifiez votre syntaxe."));
	}
	
}
