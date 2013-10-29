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
package controllers;

import java.io.File;
import java.io.IOException;

import models.Image;
import models.Question;

import org.apache.commons.io.FileUtils;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

/**
 * Methods called by ajax for editing questions
 * (pour l'instant inutile !)
 * @author malik
 *
 */
public class QuestionEdit extends Controller {
	
	/**
	 * Télécharge les images dans les réponses
	 * @return
	 */
	public static Result uploadForReponses(){
		MultipartFormData body = request().body().asMultipartFormData();
		DynamicForm info = Form.form().bindFromRequest();
		FilePart fp = body.getFile("reponseImages");
		int reponsePosition = Integer.parseInt(info.get("position"));
		Long questionId = Long.parseLong(info.get("question"));
		if(UploadImages.isImage(fp)){
			Image i = new Image(fp.getFilename());
			File image = fp.getFile();
			File destinationFile = new File(play.Play.application().path().getAbsolutePath() + "/img/" + i.fileName);
		    try{
		    	FileUtils.copyFile(image, destinationFile);
		    	i.save();
				Question question = Question.find.byId(questionId);
				question.reponses.get(reponsePosition).image=i;
				question.save();
		    	return ok("<img class=\"image\" src=\"/images/"+i.fileName+"\">");
		    } catch (IOException e){
		    	e.printStackTrace();
		    	System.out.println("Impossible de copier l'image sur le serveur...");
		    	return internalServerError("Impossible de copier l'image sur le serveur...");
		    }
		}else return badRequest("Le fichier que vous avez sélectionné n'est pas reconnu comme une image.");
	}
}
