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

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Column;



import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Contient les réponses d'un élève à une question de type 3 ou 4.
 * @author Admin
 *
 */
@SuppressWarnings("serial")
@Entity
public class Repond extends Model {
	@Id
	public Long id;
	
	@Required
	@Column(columnDefinition = "TEXT")
	public String texte;
	@Required
	public Date date;
	
	@OneToOne
	public Question question;
	@OneToOne
	public Eleve eleve;
	
	public static Finder<Long,Repond> find = new Finder<Long,Repond>(Long.class, Repond.class);

	public static List<Repond> page(){
		return find.all();
	}
	
	public static void addRepond(Repond r){
		r.save();
	}
	
	public static void removeRepond(Long id){
		Repond r = Repond.find.byId(id);
		if(r != null){
			r.delete();
		}
	}
	
	/**
	 * Vérifie si la réponse est déjà dans une liste de couple "Répond,Integer".
	 * Voir CoupleRI pour plus de détails.
	 * @param list
	 * @return La position de la réponse dans la liste si elle est déjà présente, -1 sinon.
	 */
	public int isIn(List<CoupleRI> list){
		int i=0;
		while(i<list.size()){
			if(list.get(i).repond.texte.equals(texte)){
				return i;
			}
			i++;
		}
		return -1;
	}
}