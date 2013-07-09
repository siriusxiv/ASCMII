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
import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.data.validation.Constraints.*;



@Entity
public class Seance extends Model {
	@Id
	public Long id;
	
	@Required
	public Date date;
	@Required
	public String matiere;
	@Required
	public String intitule;
	
	@ManyToOne
	public Professeur professeur;
	
	@OneToMany(targetEntity = Serie.class)
	public List<Serie> series;
	
	public static Finder<Long,Seance> find = new Finder<Long,Seance>(Long.class, Seance.class);

	public static List<Seance> page(HashMap<String,String> session){
		Professeur prof = Professeur.find.ref(session.get("username"));
		return find
				.where()
					.eq("professeur",prof)
				.orderBy("date")
				.findList();
	}
	
	public static void addSeance(Seance se){
		se.save();
	}
	
	public static void removeSeance(Long id){
		Seance se = Seance.find.byId(id);
		if(se != null){
			List<Serie> ss = Serie.find.where().eq("seance",se).findList();
			for(Serie s: ss){
				Serie.removeSerie(s.id);
			}
			se.delete();
		}
	}
	
	public static Long idNonUtilisee(){
		List<Seance> seanceTemp = Seance.find.orderBy("id desc").findList();
		if(!seanceTemp.isEmpty()){
			return seanceTemp.get(0).id+1;
		}else{
			return 1L;
		}
	}
}