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

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Column;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Contient les réponses d'un élève à une question de type 3 ou 4.
 * @author Admin
 *
 */
@Entity
public class Repond extends Model {
	@Id
	public Long id;
	
	@Required
	@Column(columnDefinition = "TEXT")
	public String texte;
	@Required
	public Date date;
	
	@OneToOne
	public Question question;
	@OneToOne
	public Eleve eleve;
	
	public static Finder<Long,Repond> find = new Finder<Long,Repond>(Long.class, Repond.class);

	public static List<Repond> page(){
		return find.all();
	}
	
	public static void addRepond(Repond r){
		r.save();
	}
	
	public static void removeRepond(Long id){
		Repond r = Repond.find.byId(id);
		if(r != null){
			r.delete();
		}
	}
	
	/**
	 * Vérifie si la réponse est déjà dans une liste de couple "Répond,Integer".
	 * Voir CoupleRI pour plus de détails.
	 * @param list
	 * @return La position de la réponse dans la liste si elle est déjà présente, -1 sinon.
	 */
	public int isIn(List<CoupleRI> list){
		int i=0;
		while(i<list.size()){
			if(list.get(i).repond.texte.equals(texte)){
				return i;
			}
			i++;
		}
		return -1;
	}
}