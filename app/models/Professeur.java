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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;


/**
 * Contient les professeurs
 * @author Admin
 *
 */
@SuppressWarnings("serial")
@Entity
public class Professeur extends Model {
	@Id
	public String username;
	
	@Required
	public String mail;
	
	@Required
	public String prenom;
	
	@Required
	public String nom;
	
	
	public static Finder<String,Professeur> find = new Finder<String,Professeur>(String.class, Professeur.class);


	public static List<Professeur> page(){
		return find.all();
	}
	
	public static void addItem(Professeur pr){
		pr.save();
	}
	
	public static void removeProfesseur(String username){
		Professeur pr = Professeur.find.byId(username);
		if(pr != null){
			pr.delete();
		}
	}
	
}