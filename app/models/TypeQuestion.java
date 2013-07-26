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
import play.data.validation.Constraints.*;

/**
 * Contient les types de questions. Voir conf/evolutions/2.sql pour voir les détails
 * de ce qu'il y a dans cette table.
 * @author Admin
 *
 */
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
