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

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import play.Application;
import play.GlobalSettings;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

/**
 * Pour planifier ce que fait le serveur au démarrage, notamment le déclenchement
 * des timers pour envoyer les mails automatiquement.
 * @author Admin
 *
 */
public class Global extends GlobalSettings{

	/**
	 * Initialisation des timers
	 */
	@Override
	public void onStart(Application app) {
		scheduler(6);
	} 
	
	/**
	 * Exécute les actions de whatIsNeededToBeDone toutes les X minutes, ou
	 * X est déterminé par la variable "duree".
	 * @param duree : indique la duree après laquelle l'action sera faite.
	 */
	void scheduler(final int duree){
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
	void whatIsNeededToBeDone(){
	    controllers.Events.sendMails();
	}
}
