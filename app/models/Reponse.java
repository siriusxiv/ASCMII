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
import play.data.DynamicForm;
import play.data.format.*;
import play.data.validation.*;
import play.data.validation.Constraints.*;

/**
 * Contient les réponses prédifinies par un professeur pour les questions
 * de type 1 ou 2. On a définit une relation d'ordre pour trier les réponses
 * selon leur position.
 * @author Admin
 *
 */
@Entity
public class Reponse extends Model implements Comparator<Reponse>{
	@Id
	public Long id;
	
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