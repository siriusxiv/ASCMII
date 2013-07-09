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
public class Reponse extends Model implements Comparator<Reponse>{
	@Id
	public Long id;
	
	@Required
	public String texte;
	@Required
	public int position;
	
	@ManyToOne
	public Question question;
	@OneToMany(targetEntity = Choisit.class)
	public List<Choisit> estChoisie;
	
	
	public static Finder<Long,Reponse> find = new Finder<Long,Reponse>(Long.class, Reponse.class);

	@Override
	public int compare(Reponse r1,Reponse r2){
			return (r1.position<r2.position ? -1 : (r1.position==r2.position ? 0 : 1));
	}
	
	public static void addReponse(Reponse re){
		re.save();
	}
	
	public static void removeReponse(Long id){
		Reponse re = Reponse.find.ref(id);
		if(re != null){
			List<Choisit> ch = Choisit.find.where().eq("reponse", re).findList();
			for(Choisit c : ch){
				Choisit.removeChoisit(c.id);
			}
			re.delete();
		}
	}
}