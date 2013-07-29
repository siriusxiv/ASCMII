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

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Fait le lien entre un élève et la réponse qu'il choisit (pour une question de type 1 ou 2).
 * @author Admin
 *
 */
@SuppressWarnings("serial")
@Entity
public class Choisit extends Model {
	@Id
	public Long id;
	
	@Required
	public Date date;
	
	@OneToOne
	public Reponse reponse;
	@OneToOne
	public Eleve eleve;
	
	public static Finder<Long,Choisit> find = new Finder<Long,Choisit>(Long.class, Choisit.class);

	public static List<Choisit> page(){
		return find.all();
	}
	
	public static void addChoisit(Choisit c){
		c.save();
	}
	
	public static void removeChoisit(Long id){
		Choisit c = Choisit.find.byId(id);
		if(c != null){
			c.delete();
		}
	}
	
}