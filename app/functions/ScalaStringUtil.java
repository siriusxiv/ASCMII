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

import java.util.List;

import models.Eleve;
import functions.agap.Groupe;
import functions.agap.Matiere;

/**
 * Un ensemble de fonction utiles pour gérer les strings dans les
 * templates.
 * @author malik
 *
 */
public class ScalaStringUtil {
	/**
	 * Rajoute un caractère d'échappement.
	 * Utile pour intégrer du Scala dans du JavaScript.
	 * (voir page gérer.scala.html)
	 * @param str
	 * @return
	 */
	public static String addEscChar(String str){
		return str.replace("'", "\'");
	}

	/**
	 * Renvoie la liste des matières sous la forme d'une string
	 * exploitable par la fonction autocomplete de jQuery
	 * (utilisé dans les templates seancesListe et editSeance).
	 * @return une chaîne du type "'Matière1','Matière2'..."
	 */
	public static String listMatieres(){
		String liste = "";
		if(AGAPUtil.listMatieres.isEmpty()){
			return liste;
		}else{
			for(Matiere mat : AGAPUtil.listMatieres){
				liste+="'"+mat.libellecourt+mat.semestre+"',";
			}
			return liste.substring(0, liste.length()-1);

		}
	}
	
	/**
	 * Renvoie la liste des groupes sous la forme d'une string
	 * exploitable par la fonction autocomplete de jQuery
	 * (utilisé dans les templates seancesListe et editSeance).
	 * @return une chaîne du type "'Groupe1','Groupe2'..."
	 */
	public static String listGroupes(){
		String liste = "";
		if(AGAPUtil.listGroupes.isEmpty()){
			return liste;
		}else{
			for(Groupe g : AGAPUtil.listGroupes){
				liste+="'"+g.getName()+"',";
			}
			return liste.substring(0, liste.length()-1);

		}
	}
	
	/**
	 * Renvoie la liste des eleves sous la forme d'une string
	 * exploitable par la fonction autocomplete de jQuery
	 * (utilisé dans le template manageGroups).
	 * @return une chaîne du type "'Eleve1','Eleve2'..."
	 */
	public static String listEleves(){
		String liste = "";
		List<Eleve> eleves = Eleve.find.all();
		if(eleves.isEmpty()){
			return liste;
		}else{
			for(Eleve e : eleves){
				liste+="'"+e.uid+"',";
			}
			return liste.substring(0, liste.length()-1);

		}
	}
}
