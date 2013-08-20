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

import java.util.concurrent.TimeUnit;
import java.lang.Throwable;

import models.Mail;

import controllers.P404;
import functions.AGAPUtil;

import play.Application;
import play.GlobalSettings;
import play.libs.Akka;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import scala.concurrent.duration.Duration;

/**
 * Pour planifier ce que fait le serveur au démarrage, notamment le déclenchement
 * des timers pour envoyer les mails automatiquement.
 * @author Admin
 *
 */
public class Global extends GlobalSettings{

	/**
	 * Initialisation des timers et du driver de la base de donnée postgres (AGAP)
	 */
	@Override
	public void onStart(Application app) {
		scheduler(6);
		AGAPUtil.init();
	} 
	
	/**
	 * Exécute les actions de whatIsNeededToBeDone toutes les X minutes, ou
	 * X est déterminé par la variable "duree".
	 * @param duree : indique la duree après laquelle l'action sera faite.
	 */
	private void scheduler(final int duree){
		whatIsNeededToBeDone();
		Akka.system().scheduler().scheduleOnce(
				  Duration.create(duree, TimeUnit.MINUTES),
				  new Runnable() {
				    public void run() {
						whatIsNeededToBeDone();
						/*if(Calendar.HOUR_OF_DAY>=20 || Calendar.HOUR_OF_DAY<7){
							scheduler(660);
						}else{*/
							scheduler(duree);
						//}
				    }
				  },
				  Akka.system().dispatcher()
				); 
	}
	
	/**
	 * Liste de ce qui doit être fait périodiquement.
	 */
	private void whatIsNeededToBeDone(){
	    functions.Events.sendMails();
	}
	
	/**
	 * Déchargement du driver postgres
	 */
	@Override
	public void onStop(Application app){
		AGAPUtil.release();
	}
	
	/**
	 * Envoie le rapport d'erreur à l'administrateur et redirige vers une page d'erreur customisée.
	 */
	@Override
	public Result onError(RequestHeader arg0, Throwable t){
		new Mail(arg0,t);
		return P404.errorPage(arg0,t);
	}
}
