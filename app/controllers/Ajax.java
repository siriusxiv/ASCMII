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

import models.Lien;
import models.Serie;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Contient les fonctions qui sont appelées par Ajax pour le rafraîchissement
 * automatique des pages.
 * @author Admin
 *
 */
public class Ajax extends Controller{

	/**
	 * Permet la fermeture de la page où l'élève répond à la question via Ajax
	 * lorsqu'une série est close et que l'élève n'a pas encore répondu. Renvoie
	 * "0" si la fin de la série n'est pas encore fixée.
	 * @param serie_id
	 * @return "0" si la fin de la série n'est pas fixée, autre chose sinon
	 */
	public static Result infoHeure(Long serie_id){
		Serie serie = Serie.find.ref(serie_id);
		if(serie.date_fermeture!=null){
			return ok(Long.toString(serie.date_fermeture.getTime()));
		}else{
			return ok("0");
		}
	}
	
	/**
	 * Permet d'avoir la proportion du nombre de répondant. (Pour rafraîchir la barre
	 * de chargement du template resultatEnCours avec ajax)
	 * @param serie_id
	 * @return un int compris entre 0 et 100 transformé en string valant la proportion
	 * du nombre de répondants en pourcent.
	 */
	
	public static Result infoNReponses(Long serie_id){
		Serie serie = Serie.find.ref(serie_id);
		return ok(String.valueOf(
					Lien.find.where().eq("repondu", true).eq("serie",serie).findList().size()*100/
					Lien.find.where().eq("serie",serie).findList().size()
				));
	}
	/**
	 * Permet de tester si oui où non la série a commencé dans serieNonCommencee.scala.html
	 * @param serie_id
	 * @return "0" si la série a commencé, "1" sinon
	 */
	public static Result hasSerieBegun(String chemin){
		Lien lien = Lien.find.ref(chemin);
		if(lien.serie.date_ouverte!=null){
			return ok("1");
		}else{
			return ok("0");
		}
	}
	
}