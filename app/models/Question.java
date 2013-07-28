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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Contient les questions. On créée de plus une relation d'ordre entres les questions
 * avec un comparateur.
 * @author Admin
 *
 */
@Entity
public class Question extends Model implements Comparator<Question>{
	@Id
	public Long id;
	
	@Required
	public String titre;
	@Required
	@Column(columnDefinition = "LONGTEXT")
	public String texte;
	@Required
	public Long position;
	
	@ManyToOne
	public TypeQuestion typeQ;
	
	@ManyToOne
	public Serie serie;
	
	@OneToMany(targetEntity = Reponse.class)
	public List<Reponse> reponses;
	
	@OneToMany(targetEntity = Repond.class)
	public List<Repond> estRepondue;
	
	public static Finder<Long,Question> find = new Finder<Long,Question>(Long.class, Question.class);
	
	/**
	 * La relation d'ordre est définie à partir des positions des questions.
	 */
	@Override
	public int compare(Question q1,Question q2){
			return (q1.position<q2.position ? -1 : (q1.position==q2.position ? 0 : 1));
	}
	
	public static void addItem(Seance se){
		se.save();
	}
	
	/**
	 * On supprime en cascade les élément de classe "Reponse" et "Repond".
	 * @param id : id de la question qu'on supprime
	 */
	public static void removeQuestion(Long id){
		Question q = Question.find.byId(id);
		if(q != null){
			List<Reponse> rs = Reponse.find.where().eq("question", q).findList();
			for(Reponse r : rs){
				Reponse.removeReponse(r.id);
			}
			List<Repond> re = Repond.find.where().eq("question", q).findList();
			for(Repond r : re){
				Repond.removeRepond(r.id);
			}
			q.delete();
		}
	}
	
	public static Long positionMax(){
		List<Question> qTemp = Question.find.orderBy("position desc").findList();
		if(!qTemp.isEmpty()){
			return qTemp.get(0).position;
		}else{
			return -1L;
		}
	}
	
	public static Long idNonUtilisee(){
		List<Question> qTemp = Question.find.orderBy("id desc").findList();
		if(!qTemp.isEmpty()){
			return qTemp.get(0).id+1;
		}else{
			return 1L;
		}
	}
}