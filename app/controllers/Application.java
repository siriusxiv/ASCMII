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
    public static Result profSeancesListe() {
    	return ok(seancesListe.render(Seance.page()));
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
			return profSeancesListe();
		}
	}
	
	
	public static Result addSeance(){
		Form<Seance> seanceForm = Form.form(Seance.class).bindFromRequest();
		if(!seanceForm.hasErrors()){
			Seance se = seanceForm.get();
			se.professeur=Professeur.find.ref(session("username"));
			Seance.addSeance(se);
		}
		return profSeancesListe();
	}
	
}
