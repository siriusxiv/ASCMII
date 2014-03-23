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
package admin;

import java.io.File;

import functions.LDAP;
import models.*;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Contain methods to clean the server
 * @author malik
 *
 */
public class Clean extends Controller{
	/**
	 * Delete temp files the user can download
	 * @return
	 */
	public static int delete(){
		return deleteFiles("csv","seriexport","seancexport");
	}

	private static int deleteFiles(String... extensions){
		File dir = new File(play.Play.application().path().getAbsolutePath());
		File[] files = dir.listFiles();
		int counter = 0;
		for(int i = 0; i<files.length; i++){
			for(int j = 0; j<extensions.length ; j++){
				if(files[i].isFile() && files[i].getName().endsWith(extensions[j])){
					files[i].delete();
					counter++;
				}
			}
		}
		return counter;
	}
	
	/**
	 * Remet à zéro la base de données et recharge tous les élèves
	 * et professeurs.
	 * @return
	 */
	public static Result reset(){
		for(Choisit l : Choisit.find.all())
			l.delete();
		for(Repond l : Repond.find.all())
			l.delete();
		for(Lien l : Lien.find.all())
			l.delete();
		for(EleveHasGroupe l : EleveHasGroupe.find.all())
			l.delete();
		for(Reponse l : Reponse.find.all())
			l.delete();
		for(Question l : Question.find.all())
			l.delete();
		for(Serie l : Serie.find.all())
			l.delete();
		for(Seance l : Seance.find.all())
			l.delete();
		for(EleveGroupe l : EleveGroupe.find.all())
			l.delete();
		for(Eleve l : Eleve.find.all())
			l.delete();
		for(Professeur l : Professeur.find.all())
			l.delete();
		for(Image l : Image.find.all())
			l.delete();
		
		new LDAP().aspireElevesEtProfesseurs();
		return ok("La suppression s'est déroulée avec succès.");
	}
}