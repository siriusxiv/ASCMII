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
import java.util.Calendar;
import java.util.Date;

import play.mvc.Http.RequestHeader;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

/**
 * Cette classe contient les infos relatives à l'envoie d'un mail.
 * Attention ! On ne peut pas envoyer de mails si on utilise le réseau Wifi de Centrale, il faut
 * être branché en Ethernet !
 * On doit bien régler le nom du domaine avant de déployer l'application et décommenter la ligne
 * mail.addRecipient(recipient,to); de la fonction sendMail() (après avoir mis en commentaire la
 * ligne juste au-dessus).
 * @author Admin
 *
 */
public class Mail{
	private static String domain_name = play.Play.application().configuration().getString("domain.url");
	private static String admin_mail = play.Play.application().configuration().getString("admin.mail");
	
	private static String from = "ASCMII <"+play.Play.application().configuration().getString("smtp.user")+">";
	
	private String sujet;
	private String contenu;
	private String to;
	private String recipient;
	
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
		contenu="<html>Bonjour,<br>"+eleve.prenom+" "+eleve.nom+", vous trouverez ci-dessous les séries de questions concernant le cours de "+
				seance.matiere+" le "+df.format(seance.date)+" :<br><br>";
		int i=1;
		for(Serie s : seance.series){
			Lien lien = Lien.find.where().eq("serie", s).eq("eleve",eleve).findUnique();
			contenu+="Série "+i+" : "+domain_name+"/eleve/"+lien.chemin+"<br>";
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
		contenu="<html>Bonjour,<br>Concernant la séance de "+seance.matiere+" le "+df.format(seance.date)+", le mail contenant les liens pour répondre aux questions a été envoyé à tous les élèves suivant ce cours.<br>Bonne journée.<br><br><span style=\"font-size:75%;float:right;\">Ce mail est un message automatique, il ne sert à rien d'y répondre.</span></html>";
	}
	
	/**
	 * Message d'erreur retransmis par mail et envoyé directement.
	 * @param arg0
	 * @param t
	 */
	public Mail(RequestHeader arg0, Throwable t){
		Date now = Calendar.getInstance().getTime();
		sujet="ASCMII : Error occured on " + now;
		contenu="The following error occurred on "+now+":\rRequestHeader : "+arg0+"\rThrowable : "+t;
		MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
		mail.setSubject(sujet);
		mail.addRecipient(admin_mail);
		mail.addFrom(from);
		mail.send(contenu);
	}
	
	/**
	 * Envoie le mail
	 */
	public void sendMail(){
			MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
			mail.setSubject(sujet);
			//mail.addRecipient(admin_mail);
			mail.addRecipient(recipient);
			mail.addFrom(from);
			mail.sendHtml(contenu);
			System.out.println(contenu);
	}
	
}

