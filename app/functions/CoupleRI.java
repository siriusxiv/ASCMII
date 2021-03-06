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

import java.util.Comparator;

import models.Repond;



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
	
	public CoupleRI(Repond r, Integer i_){
		repond=r;
		i=i_;
	}
	
	/**
	 * Permet de trier le nombre de réponse par ordre décroissant
	 */
	@Override
	public int compare(CoupleRI a,CoupleRI b){
			return (a.i<b.i ? -1 : (b.i==b.i ? 0 : 1));
	}
	
}