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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Stocke les références des images dans la base de donnée
 * (son nom de fichier suffit car elles sont dans le dossier
 * "public/uploads/".
 * @author Admin
 *
 */
@Entity
public class Image extends Model{
	@Id
	public Long id;
	
	@Required
	public String fileName;
	
	/**
	 * Crée un objet de type image ayant une ID pré-choisie et un nom formaté.
	 * @param filename
	 */
	public Image(String filename){
		id=idNonUtilisee();
		fileName=id+"_"+filename;
	}
	
	public static Finder<Long,Image> find = new Finder<Long,Image>(Long.class, Image.class);
	
	/**
	 * Supprime la référence de l'image dans la base de donnée et le fichier en lui-même
	 * stocké dans "/public/uploads" si celui-ci n'est pas utilisée par autre réponse.
	 * Attention, la référence à l'image dans la table "Reponse" n'est pas supprimée par cette fonction !
	 * Il faut la supprimer au préalable. 
	 * @param id
	 */
	public static void removeImage(Long id){
		Image i = Image.find.byId(id);
		if(i!=null){
			if(Reponse.find.where().eq("image",i).findList().isEmpty()){
				i.delete();
				try {
					Files.delete(Paths.get(play.Play.application().path().toString() + "//public//uploads//" + i.fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Trouve une ID non utilisée.
	 * @return une ID non utilisée.
	 */
	public static Long idNonUtilisee(){
		List<Image> iTemp = Image.find.orderBy("id desc").findList();
		if(!iTemp.isEmpty()){
			return iTemp.get(0).id+1;
		}else{
			return 1L;
		}
	}
	
	/**
	 * Ajoute la référence de l'image dans la base de donnée et la lie à la Reponse
	 * donnée en argument.
	 * @param reponse
	 */
	public void addImage(Reponse reponse){
		this.save();
		reponse.image=this;
		reponse.save();
	}
}