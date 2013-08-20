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

package functions;

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
	public static void sendMailAndCreateLinks(Seance seance){
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
				System.out.println("No need to send this.\n");
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
