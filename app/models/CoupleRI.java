package models;

import java.util.*;

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