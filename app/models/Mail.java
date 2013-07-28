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

