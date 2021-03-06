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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
public class EleveHasGroupe extends Model  {
	@Id
	public Long id;
	@ManyToOne(targetEntity = Eleve.class)
	public Eleve eleve;
	@ManyToOne(targetEntity = EleveGroupe.class)
	public EleveGroupe groupe;
	
	public static Finder<Long,EleveHasGroupe> find = new Finder<Long,EleveHasGroupe>(Long.class, EleveHasGroupe.class);
	
	public EleveHasGroupe(Long groupe_id, String eleve_uid){
		eleve=Eleve.find.byId(eleve_uid);
		groupe=EleveGroupe.find.byId(groupe_id);
	}
}
