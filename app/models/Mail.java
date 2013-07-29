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

package models;


import java.text.DateFormat;
import java.text.SimpleDateFormat;



import com.typesafe.plugin.*;

/**
 * Cette classe contient les infos relatives à l'envoie d'un mail.
 * Attention ! On ne peut pas envoyer de mails si on utilise le réseau Wifi de Centrale, il faut
 * être branché en Ethernet !
 * @author Admin
 *
 */
public class Mail{
	String sujet;
	String contenu;
	String to;
	String recipient;
	String from;
	
	/**
	 * Permet de créer le mail qui sera envoyé à un élève donné pour une séance donnée.
	 * Ne pas oublier de changer le nom de domaine en cas de besoin.
	 * @param eleve
	 * @param seance
	 */
	public Mail(Eleve eleve, Seance seance){
		DateFormat df = new SimpleDateFormat("dd/MM");
		sujet="[ASCMII] Lien pour les questions concernant le cours de "+seance.matiere+" le "+df.format(seance.date);
		to=eleve.mail;
		recipient=eleve.prenom+" "+eleve.nom+" <"+to+">";
		from="ASCMII <ascmii.test@gmail.com>";
		contenu="<html>Bonjour,<br>"+eleve.prenom+" "+eleve.nom+", vous trouverez ci-dessous les séries de questions concernant le cours de "+
				seance.matiere+" le "+df.format(seance.date)+" :<br><br>";
		int i=1;
		for(Serie s : seance.series){
			Lien lien = Lien.find.where().eq("serie", s).eq("eleve",eleve).findUnique();
			contenu+="Série "+i+" : http://localhost:9000/eleve/"+lien.chemin+"<br>";
			i++;
		}
		contenu+="</html>";
	}
	
	/**
	 * Permet de créer le mail qui sera envoyé à un professeur pour confirmer l'envoi du mail.
	 * @param seance
	 */
	public Mail(Seance seance){
		DateFormat df = new SimpleDateFormat("dd/MM");
		sujet="[ASCMII] Confirmation de l'envoi du mail concernant le cours de "+seance.matiere+" le "+df.format(seance.date);
		to=seance.professeur.mail;
		recipient=seance.professeur.prenom+" "+seance.professeur.nom+" <"+to+">";
		from="ASCMII <ascmii.test@gmail.com>";
		contenu="<html>Bonjour,<br>Concernant la séance de "+seance.matiere+" le "+df.format(seance.date)+", le mail contenant les liens pour répondre aux questions a été envoyé à tous les élèves suivant ce cours.<br>Bonne journée.<br><br><span style=\"font-size:75%;float:right;\">Ce mail est un message automatique, il ne sert à rien d'y répondre.</span></html>";
	}
	
	/**
	 * Envoie le mail
	 */
	public void sendMail(){
			MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
			mail.setSubject(sujet);
			mail.addRecipient("Malik Boussejra <malik.boussejra@eleves.ec-nantes.fr>","malik.boussejra@eleves.ec-nantes.fr");
			//mail.addRecipient(recipient,to);
			mail.addFrom(from);
			mail.sendHtml(contenu);
			System.out.println(contenu);
	}
	
}

