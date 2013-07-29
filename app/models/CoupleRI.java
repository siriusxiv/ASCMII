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

import java.util.Comparator;
import java.util.List;




/**
 * Définit un couple "Repond,Integer". Cela permet d'assigner à
 * chaque réponse d'un élève le nombre d'élève qui ont répondu
 * exactement la même chose. Cette classe est beaucoup utilisée
 * dans la classe "Resultat".
 * @author Admin
 *
 */
public class CoupleRI implements Comparator<CoupleRI>{
	public Repond repond;
	public Integer i;
	
	CoupleRI(Repond r, Integer i_){
		repond=r;
		i=i_;
	}
	
	/**
	 * Cette fonction est là pour générer la dernière ligne "autre" des résultats.
	 * Elle est utilisée dans Resultat.listeResultat(Serie serie).
	 * @param list
	 * @param nombreDeRepondants
	 */
	CoupleRI(List<CoupleRI> list, Integer nombreDeRepondants){
		repond = new Repond();
		repond.texte="Autres réponses : ";
		//i est égal à la somme des
		i=nombreDeRepondants;
		for(int j = 0; j<list.size();j++){
			i-=list.get(j).i;
		}
	}
	/**
	 * Permet de trier le nombre de réponse par ordre décroissant
	 */
	@Override
	public int compare(CoupleRI a,CoupleRI b){
			return (a.i<b.i ? -1 : (b.i==b.i ? 0 : 1));
	}
	
}