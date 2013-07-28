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

package controllers;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import models.Image;
import models.Reponse;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

/**
 * Toutes les fonctions concernant les images qui sont uploadées pour chaque
 * réponse.
 * @author Admin
 *
 */
public class UploadImages extends Controller{

	/**
	 * Permet d'uploader une image. L'image est enregistrée dans le dossier "/public/uploads".
	 * On stocke le nom du fichier de l'image dans la base de donnée et on le lie à une réponse.
	 * @param reponse_id
	 * @return Affiche la page de gestion d'une séance
	 */
	public static Result upload(Long reponse_id){
		Reponse reponse = Reponse.find.ref(reponse_id);
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart filePart = body.getFile("image");
		if(isImage(filePart)){
			File image = filePart.getFile();
			String fileName = filePart.getFilename();
	    	Image i = new Image(fileName);
			File destinationFile = new File(play.Play.application().path().toString() + "//public//uploads//" + i.fileName);
		    System.out.println(play.Play.application().path());
		    System.out.println(image.getAbsolutePath());
		    try{
		    	FileUtils.copyFile(image, destinationFile);
		    	i.addImage(reponse);
				return SeancesListe.gererSeanceLog(reponse.question.serie.seance.id,"Image uploadée avec succès !");
		    } catch (IOException e){
		    	e.printStackTrace();
		    	return SeancesListe.gererSeanceLog(reponse.question.serie.seance.id,"Impossible de copier l'image sur le serveur...");
		    }
		}else{
	    	return SeancesListe.gererSeanceLog(reponse.question.serie.seance.id,"Le fichier uploadé n'est pas image ou son format n'est pas supporté !");
		}
	}
	
	/**
	 * Vérifie que le fichier est bien une image
	 * @param filePart : le fichier que l'on teste
	 * @return VRAI ou FAUX
	 */
	static boolean isImage(FilePart filePart){
		if(filePart!=null){
			return filePart.getContentType().startsWith("image");
		}else{
			return false;
		}
	}
	
	/**
	 * Supprime une image du serveur, mais aussi sa référence dans la base de donnée.
	 * Question ? : Doit-on supprimer l'image de la base de donnée si elle n'est plus utilisée ?
	 * Imaginons qu'un prof exporte une série et la donne à un collègue. Si ce prof supprime
	 * l'image avant que son collègue n'ait importé la série, alors il n'y aura pas d'image
	 * affichée pour son collègue.
	 * Cependant, pour l'instant, une image n'est pas supprimée (ni de la base de donnée, ni du
	 * disque dur du serveur) tant qu'elle est utilisée par au moins une réponse.
	 * Notez qu'on met "null" dans la colonne "image_id" de la table "Reponse" avant de supprimer
	 * l'image.
	 * @param reponse_id
	 * @return Affiche la page de gestion d'une séance
	 */
	public static Result deleteImage(Long reponse_id){
		Reponse reponse = Reponse.find.ref(reponse_id);
		Image imgTemp = reponse.image;
		reponse.image=null;
		reponse.save();
		Image.removeImage(imgTemp.id);
		return SeancesListe.gererSeanceLog(reponse.question.serie.seance.id,"L'image a été supprimée avec succès.");
	}
}