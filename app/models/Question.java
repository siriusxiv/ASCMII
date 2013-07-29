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
@SuppressWarnings("serial")
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
	 * Pour rétrocompatibilité
	 */
	public Question(){}
	
	/**
	 * Copie la question telle quelle en la liant à la série en argument.
	 * (utilisée dans dupliquerSeance)
	 * @param reponse
	 * @param _question
	 */
	public Question(Question question, Serie _serie){
		texte=question.texte;
		titre=question.titre;
		typeQ=question.typeQ;
		serie=_serie;
		position=question.position;
		id=Question.idNonUtilisee();
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