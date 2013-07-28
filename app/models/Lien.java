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

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Contient les liens unique entre chaque élève et chaque série.
 * Un lien relie un élève et une série car chaque série a une URL unique pour
 * chaque élève.
 * @author Admin
 *
 */
@Entity
public class Lien extends Model {
	@Id
	public String chemin;
	@Required
	public boolean repondu;
	
	@OneToOne
	public Serie serie;
	@OneToOne
	public Eleve eleve;
	
	public static Finder<String,Lien> find = new Finder<String,Lien>(String.class, Lien.class);

	public static List<Lien> page(){
		return find.all();
	}
	
	/**
	 * Ajoute un lien dans la base de donnée à partir d'un élève et d'une série.
	 * Le chemin du lien doit avoir l'être aléatoire. On génère ce chemin avec les
	 * hashCode des classes Eleve et Serie convertis en chaîne hexadécimale.
	 * @param eleve
	 * @param serie
	 */
	public static void addLien(Eleve eleve, Serie serie){
		if(Lien.find.where().eq("eleve",eleve).eq("serie",serie).findList().isEmpty()){//Si un tel lien exist déjà, pas besoin de le rajouter
			int a = eleve.hashCode();
			int b = serie.hashCode();
			int c = a*b;
			String chemin = Integer.toHexString(c);
			while(find.byId(chemin)!=null){
				c*=2;
				chemin=Integer.toHexString(c);
			}
			Lien lien = new Lien();
			lien.chemin = chemin;
			lien.serie=serie;
			lien.eleve=eleve;
			lien.repondu=false;
			lien.save();
		}
	}
	
	/**
	 * Vérifie que, pour une lien donnée, il n'est pas trop tard pour répondre à la question
	 * @return Vrai ou Faux
	 */
	public boolean aLHeure(){
		return (serie.date_ouverte!=null &&
					(serie.date_fermeture==null ||
						Calendar.getInstance().getTime().compareTo(serie.date_fermeture)<=0
					)
				);
	}
	
	public static void removeLien(String chemin){
		Lien l = Lien.find.byId(chemin);
		if(l != null){
			l.delete();
		}
	}
	
}