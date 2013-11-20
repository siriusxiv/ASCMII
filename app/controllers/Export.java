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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

import functions.agap.Matiere;



import models.Image;
import models.Lien;
import models.Question;
import models.Reponse;
import models.Resultat;
import models.Seance;
import models.Serie;
import models.TypeQuestion;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.*;

/**
 * Ici, on gère les exports (les fichiers téléchargés par l'utilisateur)
 * @author Admin
 *
 */
public class Export extends Controller{

	/**
	 * Provoque le téléchargement de la liste exhaustive des réponses à une question.
	 * @param question_id
	 * @return le fichier que l'on va télécharger
	 */
	public static Result downloadExhaustive(Long question_id){
		FileWriter outputStream = null;
		String file_name = "liste_exhautive"+question_id+".html";
		try{
			outputStream = new FileWriter(file_name);
			String page = generePageExhaustive(question_id);
			outputStream.write(page);
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		FileInputStream fileIS = null;
		try {
			fileIS = new FileInputStream(new File(file_name));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		response().setHeader("Content-Disposition", "attachment; filename="+file_name);
		return ok(fileIS);
	}

	/**
	 * Génère la chaîne de caractère donnant toutes les réponses à une question donnée.
	 * Cette chaine est ensuite envoyé à l'utilisateur sous forme de fichier .html
	 * @param question_id
	 * @return chaîne de caractère donnant toutes les réponses à une question donnée
	 */
	static String generePageExhaustive(Long question_id){
		Resultat resultat = Resultat.exhaustive(question_id);
		Serie serie = Question.find.ref(question_id).serie;
		int nombreDeReponse = Lien.find.where().eq("repondu",true).eq("serie",serie).findList().size();
		int nombreDeReponseMAX = Lien.find.where().eq("serie",serie).findList().size();
		String tableStyle = "border-collapse:collapse;background-color:white;min-width: 200px;position: left;width:400px;padding:10px;";
		String tdStyle = "border: 1px solid black;1px 5px 1px 5px;";
		String tdStyleNumber ="border: 1px solid black;padding: 1px 5px 1px 5px;width:25px;text-align:right;";
		String s =	"<html><head><meta charset=\"utf-8\"></head><h2>Résultats de la question suivante :</h2><br><h2>"+resultat.question.texte+"</h2><br><table style=\""+tableStyle+"\">"
				+ "<p>"+nombreDeReponse+" personnes sur "+nombreDeReponseMAX+" ont répondu.</p>";
		for(int i = 0 ; i<resultat.listRepond.size() ; i++){
			s+="<tr><td style=\""+tdStyle+"\">"+resultat.listRepond.get(i).texte+"</td><td style=\""+tdStyleNumber+"\">"+resultat.reponsesChoisies.get(i)+"</td></tr>";
		}
		s+="</html></table>";
		return s;
	}

	/**
	 * Provoque le téléchargement de l'export d'une série de question
	 * @param serie_id
	 * @return le fichier que l'on va télécharger
	 */
	public static Result downloadSerie(Long serie_id){
		FileWriter outputStream = null;
		String file_name = serie_id+".seriexport";
		Serie serie = null;
		try{
			outputStream = new FileWriter(file_name);
			serie = Serie.find.ref(serie_id);
			String page = genereSerieExport(serie);
			outputStream.write(page);
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		FileInputStream fileIS = null;
		try {
			fileIS = new FileInputStream(new File(file_name));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		response().setHeader("Content-Disposition", "attachment; filename="+noSpace(serie.nom)+".seriexport");
		return ok(fileIS);
	}

	/**
	 * Génère la chaîne de caractère permettant d'exporter une série.
	 * Structure du fichier :
	 *  SERIE=nom de la série
	 *  QUESTION_TITRE=titre de la question
	 *  QUESTION_TYPE=id du type
	 *  QUESTION_TEXTE=la question en elle-même
	 *  REPONSE=reponse (on recommence cette ligne tant qu'il y a une réponse)
	 *  (on recommence les 4 précédentes lignes tant qu'il y a des questions)
	 * @param serie_id
	 * @return chaîne de caractère permettant d'exporter une série
	 */
	static String genereSerieExport(Serie serie){
		String s = "SERIE="+serie.nom+"\n";
		Collections.sort(serie.questions,new Question());
		for(Question q : serie.questions){
			s+="QUESTION_TITRE="+q.titre+"\nQUESTION_TYPE="+q.typeQ.id+"\nQUESTION_TEXTE="+q.texte+"\n";
			Collections.sort(q.reponses,new Reponse());
			for(Reponse r : q.reponses){
				s+="REPONSE="+r.texte+"\n";
				s+="IMAGE=";
				if(r.image!=null){
					s+=r.image.fileName;
				}
				s+='\n';
			}
		}
		return s;
	}

	/**
	 * Supprime les espaces dans une chaîne de caractères.
	 * Sert pour le générer le nom du fichier exporter.
	 * @param str : une chaîne quelconque
	 * @return la chaîne sans espace
	 */
	static String noSpace(String str){
		return str.replace(' ', '_');
	}

	/**
	 * Cette fonction appelée par une route permet de charger un fichier
	 * depuis son ordinateur.
	 * @param seance_id
	 * @return affiche la page de gestion des séries de questions ou bien
	 * un message d'erreur si le fichier est invalide
	 */
	public static Result uploadSerie(Long seance_id){
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart filePart = body.getFile("fichier");
		if(filePart != null){
			String contentType = filePart.getContentType();
			System.out.println(contentType);
			if(readFileSerieExport(filePart.getFile(),seance_id)){
				return SeancesListe.gererSeance(seance_id);
			}else{
				return ok(erreurUpload.render("Fichier non valide"));
			}
		}else{
			return ok(erreurUpload.render("filePart est null"));
		}
	}

	/**
	 * Lit le fichier file contenant les informations concernant une série de questions
	 * et ajoute ses informations dans la base de donnée.
	 * @param file : un fichier que l'utilisateur à uploadé
	 * @param seance_id : l'id de la séance pour laquelle ou va uploader le fichier
	 * @return VRAI si cela réussi, FAUX sinon
	 */
	static boolean readFileSerieExport(File file,Long seance_id){
		BufferedReader br = null;
		Serie serie = new Serie();
		boolean somethingInserted=false;
		try {
			br = new BufferedReader(new FileReader(file));
			String input;
			//On trouve le titre de la série
			if((input = br.readLine())!=null && startsWith(input,"SERIE=")){
				serie.nom=input.substring(6);
				serie.seance=Seance.find.ref(seance_id);
				serie.position=Serie.positionMax()+1;
				serie.id=Serie.idNonUtilisee();
				serie.save();
				somethingInserted=true;
				Long positionQuestion=0L;
				//on lit la ligne suivante pour ajouter les questions
				while((input = br.readLine())!=null && startsWith(input,"QUESTION_TITRE=")){
					Question q = new Question();
					q.titre=input.substring(15);
					if((input = br.readLine())!=null && startsWith(input,"QUESTION_TYPE=")){
						q.typeQ=TypeQuestion.find.ref(Long.parseLong(input.substring(14)));
						if((input = br.readLine())!=null && startsWith(input,"QUESTION_TEXTE=")){
							q.texte = input.substring(15);
							q.id=Question.idNonUtilisee();
							q.position=positionQuestion;
							positionQuestion++;
							q.serie=serie;
							q.save();
							int positionReponse = 1;
							br.mark(1000000);
							while((input = br.readLine())!=null && input.startsWith("REPONSE=")){
								Reponse r = new Reponse();
								r.position = positionReponse;
								positionReponse++;
								r.question=q;
								r.texte=input.substring(8);
								if((input = br.readLine())!=null && startsWith(input,"IMAGE=")){
									r.image=Image.find.where().eq("fileName", input.substring(6)).findUnique();
								}
								r.save();
								br.mark(1000000);
							}
						}
					}
					serie.questions.add(q);
					br.reset();
				}
				close(br);
				return true;
			}
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			close(br);
		}
		//On supprime ce qui vient d'être inséré (si ça a été inséré bien sûr) car il y a eu un problème !
		if(somethingInserted){
			Serie.removeSerie(serie.id);
		}
		return false;
	}

	/**
	 * Ferme un BufferedReader
	 * @param br
	 */
	static void close(BufferedReader br){
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remplace input.startsWith() dans readFile() par cette fonction afin
	 * de renvoyer une exception en cas de fichier mal fichu.
	 * @param input
	 * @param test
	 * @return VRAI si "input" commence par "test".
	 * @throws IOException
	 */
	static boolean startsWith(String input, String test) throws IOException{
		if(input.startsWith(test)){
			return true;
		}else{
			IOException e = new IOException("Fichier non valide (attendu : "+test+", trouvé : "+input+")");
			throw e;
		}
	}


	/**
	 * Provoque le téléchargement de l'export d'une séance
	 * @param seance_id
	 * @return le fichier que l'on va télécharger
	 */
	public static Result downloadSeance(Long seance_id){
		FileWriter outputStream = null;
		String file_name = seance_id+".seancexport";
		Seance seance = null;
		try{
			outputStream = new FileWriter(file_name);
			seance = Seance.find.ref(seance_id);
			String page = genereSeanceExport(seance);
			outputStream.write(page);
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		FileInputStream fileIS = null;
		try {
			fileIS = new FileInputStream(new File(file_name));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		response().setHeader("Content-Disposition", "attachment; filename="+noSpace(seance.intitule)+".seancexport");
		return ok(fileIS);
	}


	/**
	 * Génère la chaîne de caractère permettant d'exporter une série.
	 * Structure du fichier :
	 * 	SEANCE_INTITULE=intitulé de la séance
	 *  SEANCE_MATIERE=matiere de la séance
	 *  SEANCE_GROUPE=groupe de la séance
	 *  SERIE=nom de la série
	 *  QUESTION_TITRE=titre de la question
	 *  QUESTION_TYPE=id du type
	 *  QUESTION_TEXTE=la question en elle-même
	 *  REPONSE=reponse (on recommence cette ligne tant qu'il y a une réponse)
	 *  (on recommence les 4 précédentes lignes tant qu'il y a des questions)
	 * @param serie_id
	 * @return chaîne de caractère permettant d'exporter une série
	 */
	static String genereSeanceExport(Seance seance){
		String s = "SEANCE_INTITULE="+seance.intitule+"\nSEANCE_MATIERE="+seance.matiere+"\nSEANCE_GROUPE=";
		if(seance.groupe==null)	s+="\n";
		else					s+=seance.groupe+"\n";
		Collections.sort(seance.series,new Serie());
		for(Serie serie : seance.series){
			s+=genereSerieExport(serie);
		}
		return s;
	}


	/**
	 * Cette fonction appelée par une route permet de charger un fichier
	 * depuis son ordinateur.
	 * @return affiche la page de gestion des séries de questions ou bien
	 * un message d'erreur si le fichier est invalide
	 */
	public static Result uploadSeance(){
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart filePart = body.getFile("fichier");
		if(filePart != null){
			String contentType = filePart.getContentType();
			System.out.println(contentType);
			if(readFileSeanceExport(filePart.getFile())){
				return Login.profSeancesListe("Fichier uploadé avec succès. La nouvelle séance a été ajoutée, commencez par éditer la date à votre convenance.");
			}else{
				return Login.profSeancesListe("Fichier non valide");
			}
		}else{
			return Login.profSeancesListe("filePart est null");
		}
	}



	/**
	 * Lit le fichier file contenant les informations concernant une série de questions
	 * et ajoute ses informations dans la base de donnée.
	 * @param file : un fichier que l'utilisateur à uploadé
	 * @param seance_id : l'id de la séance pour laquelle ou va uploader le fichier
	 * @return VRAI si cela réussi, FAUX sinon
	 */
	static boolean readFileSeanceExport(File file){
		BufferedReader br = null;
		Seance seance = null;
		boolean somethingInserted=false;
		try {
			br = new BufferedReader(new FileReader(file));
			String input = "";
			//On trouve l'intitulé de la séance
			if((input = br.readLine())!=null && startsWith(input,"SEANCE_INTITULE=")){
				String username = getUserName();
				seance = new Seance(username,input.substring(16));
				if((input = br.readLine())!=null && startsWith(input,"SEANCE_MATIERE=")){
					seance.matiere=input.substring(15);
					seance.matiere_id=Matiere.getID(seance.matiere);
					if((input = br.readLine())!=null && startsWith(input,"SEANCE_GROUPE=")){
						if(input.substring(14).equals(""))	seance.groupe=null;
						else								seance.groupe=input.substring(14);
						seance.save();
						somethingInserted=true;
						Long positionSerie = 0L;
						while((input = br.readLine())!=null && startsWith(input,"SERIE=")){
							Serie serie = new Serie();
							serie.nom=input.substring(6);
							serie.seance=seance;
							serie.position=positionSerie;
							positionSerie++;
							serie.id=Serie.idNonUtilisee();
							serie.save();
							Long positionQuestion=0L;
							br.mark(1000000);
							while((input = br.readLine())!=null && input.startsWith("QUESTION_TITRE=")){
								Question q = new Question();
								q.titre=input.substring(15);
								if((input = br.readLine())!=null && startsWith(input,"QUESTION_TYPE=")){
									q.typeQ=TypeQuestion.find.ref(Long.parseLong(input.substring(14)));
									if((input = br.readLine())!=null && startsWith(input,"QUESTION_TEXTE=")){
										q.texte = input.substring(15);
										q.id=Question.idNonUtilisee();
										q.position=positionQuestion;
										positionQuestion++;
										q.serie=serie;
										q.save();
										int positionReponse = 1;
										br.mark(1000000);
										while((input = br.readLine())!=null && input.startsWith("REPONSE=")){
											Reponse r = new Reponse();
											r.position = positionReponse;
											positionReponse++;
											r.question=q;
											r.texte=input.substring(8);
											if((input = br.readLine())!=null && startsWith(input,"IMAGE=")){
												r.image=Image.find.where().eq("fileName", input.substring(6)).findUnique();
											}
											r.save();
											br.mark(1000000);
										}
									}
								}
								serie.questions.add(q);
								br.reset();
								br.mark(1000000);
							}
							br.reset();
						}
					}
				}
			}
			close(br);
			return true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			close(br);
		}
		//On supprime ce qui vient d'être inséré (si ça a été inséré bien sûr) car il y a eu un problème !
		if(somethingInserted){
			Seance.removeSeance(seance.id);
		}
		return false;
	}

	/**
	 * Renvoie l'identifiant de la personne loguée. Si personne n'est logué, throw une exception.
	 * @return l'identifiant de la personne loguée
	 * @throws IOException
	 */
	static String getUserName() throws IOException{
		if(session("username")!=null){
			return session("username");
		}else{
			throw new IOException("Vous n'êtes pas logué, certainement car vous venez de supprimer les cookies.");
		}
	}

}