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
public class Serie extends Model {
	@Id
	public Long id;
	
	@Required
	public String nom;
	
	public Date date_ouverte;
	public Date date_fermeture;
	
	@Required
	public Long position;
	
	@ManyToOne
	public Seance seance;
	
	@OneToMany(targetEntity = Question.class)
	public List<Question> questions;
	
	public static Finder<Long,Serie> find = new Finder<Long,Serie>(Long.class, Serie.class);

	public static List<Serie> page(Long id){
		Seance seance = Seance.find.ref(id);
		return find
				.where()
					.eq("seance",seance)
				.orderBy("position")
				.findList();
	}
	
	public static void addSerie(Serie se){
		se.save();
	}
	
	public static void removeSerie(Long id){
		Serie se = Serie.find.ref(id);
		if(se != null){
			List<Question> qs = Question.find.where().eq("serie",se).findList();
			for(Question q : qs){
				Question.removeQuestion(q.id);
			}
			se.delete();
		}
	}
	
	
	public static Long positionMax(){
		List<Serie> serieTemp = Serie.find.orderBy("position desc").findList();
		if(!serieTemp.isEmpty()){
			return serieTemp.get(0).position;
		}else{
			return 0L;
		}
	}
	
	public static Long idNonUtilisee(){
		List<Serie> serieTemp = Serie.find.orderBy("id desc").findList();
		if(!serieTemp.isEmpty()){
			return serieTemp.get(0).id+1;
		}else{
			return 0L;
		}
	}
}