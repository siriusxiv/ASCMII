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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.DynamicForm;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Contient les réponses prédifinies par un professeur pour les questions
 * de type 1 ou 2. On a définit une relation d'ordre pour trier les réponses
 * selon leur position.
 * @author Admin
 *
 */
@SuppressWarnings("serial")
@Entity
public class Reponse extends Model implements Comparator<Reponse>{
	@Id
	public Long id;
	
	@Required
	@Column(columnDefinition = "TEXT")
	public String texte;
	@Required
	public int position;
	
	@ManyToOne
	public Question question;
	@OneToMany(targetEntity = Choisit.class)
	public List<Choisit> estChoisie;
	
	@ManyToOne
	public Image image;
	
	public Reponse(){}
	
	public Reponse(String _texte,Question _question,int _position){
		texte=_texte;
		question=_question;
		position=_position;
	}
	
	public Reponse(Reponse reponse, Question _question){
		texte=reponse.texte;
		question=_question;
		position=reponse.position;
		image=reponse.image;
	}
	
	public static Finder<Long,Reponse> find = new Finder<Long,Reponse>(Long.class, Reponse.class);
	
	/**
	 * Relation d'ordre
	 */
	@Override
	public int compare(Reponse r1,Reponse r2){
			return (r1.position<r2.position ? -1 : (r1.position==r2.position ? 0 : 1));
	}
	
	@Override
	public String toString(){
		return position + ". "+texte;
	}
	
	public static void addReponse(Reponse re){
		re.save();
	}
	
	/**
	 * Supprime une réponse et supprime en cascade les "Choisit" et les "Image".
	 * Notez qu'on supprime la réponse avec de supprimer l'image.
	 * @param id : id de la réponse que l'on supprime
	 */
	public static void removeReponse(Long id){
		Reponse re = Reponse.find.byId(id);
		if(re != null){
			List<Choisit> ch = Choisit.find.where().eq("reponse", re).findList();
			for(Choisit c : ch){
				Choisit.removeChoisit(c.id);
			}
			re.delete();
			if(re.image!=null){
				Image.removeImage(re.image.id);
			}
		}
	}
	
	/**
	 * Renvoie la liste des réponses à partir d'une forme.
	 * @param info : la forme contenant les réponses
	 * @param q : question à laquelle appartiennent les réponses
	 * @return la liste des réponses
	 */
	public static List<Reponse> reponsesForm(DynamicForm info,Question q){
		List<Reponse> reponses = new ArrayList<Reponse>();
		int i = 1;
		String reponseTexte = null;
		while((reponseTexte = info.get("reponse"+i))!=null){
			reponses.add(new Reponse(reponseTexte,q,i));
			i++;
		}
		return reponses;
	}
	
	/**
	 * Vérifie si la réponse r est dans la liste des réponses list.
	 * @param list : une liste
	 * @param r : une réponse
	 * @return sa position s'il est dans la liste, -1 sinon
	 */
	public static int isItIn(List<Reponse> list,Reponse r){
		boolean isIn = false;
		int i = 0;
		while(!isIn && i<list.size()){
			isIn=list.get(i).texte.equals(r.texte);
			i++;
		}
		if(isIn){
			return i-1;
		}else{
			return -1;
		}
	}
}