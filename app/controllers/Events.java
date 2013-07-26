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

import java.util.Calendar;
import java.util.List;

import models.Eleve;
import models.Lien;
import models.Mail;
import models.Seance;
import models.Serie;


/**
 * Cette classe contient les méthodes relatives à la gestion du temps.
 * Du coup, elle ne contient que les choses relatives à l'envoie des mails et
 * la vérification de cet envoie.
 * @author Admin
 *
 */
public class Events {
	
	/**
	 * Envoie les mails s'une séance donnée aux élèves concernés.
	 * @param seance
	 */
	static void sendMailAndCreateLinks(Seance seance){
		List<Eleve> eleves = find(seance);
		for(Eleve e : eleves){
			for(Serie s : seance.series){
				Lien.addLien(e, s);
			}
			Mail mail = new Mail(e,seance);
			mail.sendMail();//on envoie le mail
		}
		//On envoie aussi un mail au professeur pour confirmer l'envoie du mail :
		Mail mail = new Mail(seance);
		mail.sendMail();
	}
	
	/**
	 * Cette fonction trouve les élèves concernés par telle séance.
	 * Pour l'instant, on va supposer que tous les élèves doivent répondre
	 * à la série de question
	 * @param seance
	 * @return la liste des élèves concernés
	 */
	static List<Eleve> find(Seance seance){
		return Eleve.find.all();
	}
	
	
	/**
	 * Vérifie si oui ou non le mail de la séance doit être envoyé.
	 * On envoie le mail 15 minutes avant le début de la séance.
	 * @param seance
	 * @return VRAI ou FAUX
	 */
	static boolean checkIfNeedToBeSend(Seance seance){
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, 15);
		return !seance.series.isEmpty()
				&& !seance.hasEmptySerie()
				&& seance.series.get(0).liens.isEmpty()
				&& now.getTime().after(seance.date)
				;
	}
	
	
	/**
	 * Envoie tous les mails qu'il faut envoyer.
	 * Cette fonction s'exécute toutes les 5 minutes grâces à la fonction sendMails.
	 */
	static void checkAllAndSend(){
		System.out.println("Starting to check if there are mails to send...\n");
		List<Seance> seances = Seance.find.all();
		for(Seance s : seances){
			if(checkIfNeedToBeSend(s)){
				sendMailAndCreateLinks(s);
				System.out.println("Mail sent\n");
			}else{
				System.out.println("No need do send this.\n");
			}
		}
		System.out.println("Finished.\n");
	}
	
	/**
	 * Fait s'exécuter la méthode checkAllAndSend() toutes les 6 minutes.
	 * Cette méthode doit être lancée automatiquement au démarrage du serveur.
	 * (ceci est fait grâce à la classe Global)
	 */
	public static void sendMails(){
		checkAllAndSend();
	}
}
