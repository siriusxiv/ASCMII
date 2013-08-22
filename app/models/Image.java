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
@SuppressWarnings("serial")
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
		fileName=id+"_"+noSpace(filename);
	}
	
	/**
	 * Supprime les espaces.
	 * @param str
	 * @return la chaîne str sans espaces
	 */
	private static String noSpace(String str){
		return str.replace(' ', '_');
	}
	
	public static Finder<Long,Image> find = new Finder<Long,Image>(Long.class, Image.class);
	
	/**
	 * Supprime la référence de l'image dans la base de donnée et le fichier en lui-même
	 * stocké dans "/public/uploads" si celui-ci n'est pas utilisée par autre réponse.
	 * Attention, la référence à l'image dans la table "Reponse" n'est pas supprimée par
	 * cette fonction !
	 * Il faut la supprimer au préalable. 
	 * 
	 * J'ai fait le choix de ne jamais supprimer une image de la base de donnée. Donc, j'ai
	 * tour mis en commenantaire.
	 * @param id
	 */
	public static void removeImage(Long id){
		/*Image i = Image.find.byId(id);
		if(i!=null){
			if(Reponse.find.where().eq("image",i).findList().isEmpty()){
				i.delete();
				try {
					java.nio.file.Files.delete(java.nio.file.Paths.get(play.Play.application().path().getAbsolutePath() + "/img/" + i.fileName));
				} catch (java.io.IOException e) {
					e.printStackTrace();
				}
			}
		}*/
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