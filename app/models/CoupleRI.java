package models;

import java.util.*;

public class CoupleRI implements Comparator<CoupleRI>{
	public Repond repond;
	public Integer i;
	
	CoupleRI(Repond r, Integer i_){
		repond=r;
		i=i_;
	}
	
	//Cela permet de trier le nombre de réponse par ordre décroissant
	@Override
	public int compare(CoupleRI a,CoupleRI b){
			return (a.i>b.i ? -1 : (b.i==b.i ? 0 : 1));
	}
	
}