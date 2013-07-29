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
 * Contient les types de questions. Voir conf/evolutions/2.sql pour voir les détails
 * de ce qu'il y a dans cette table.
 * @author Admin
 *
 */
@SuppressWarnings("serial")
@Entity
public class TypeQuestion extends Model{
	@Id
	public int id;
	
	@Required
	public String typeQ;
	
	public static Finder<Long,TypeQuestion> find = new Finder<Long,TypeQuestion>(Long.class,TypeQuestion.class);
	
	public static List<TypeQuestion> page(){
		return find.all();
	}
	
	public static void addType(TypeQuestion typeQ){
		typeQ.save();
	}
	
	public static void removeType(Long id){
		TypeQuestion typeQ = TypeQuestion.find.byId(id);
		if(typeQ != null){
			typeQ.delete();
		}
	}
	
}
