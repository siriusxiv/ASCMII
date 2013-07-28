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