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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

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
			if(readFile(filePart.getFile(),seance_id)){
				return Application.gererSeance(seance_id);
			}else{
				return ok("Fichier non valide");
			}
		}else{
			return ok("filePart est null");
		}
	}
	
	/**
	 * Lit le fichier file contenant les informations concernant une série de questions
	 * et ajoute ses informations dans la base de donnée.
	 * @param file : un fichier que l'utilisateur à uploadé
	 * @param seance_id : l'id de la séance pour laquelle ou va uploader le fichier
	 * @return VRAI si cela réussi, FAUX sinon
	 */
	static boolean readFile(File file,Long seance_id){
		BufferedReader br = null;
		Serie serie = new Serie();
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
				Long positionQuestion=0L;
				//on lit la ligne suivante pour ajouter les questions
				while((input = br.readLine())!=null && startsWith(input,"QUESTION_TITRE=")){
					Question q = new Question();
					q.titre=input.substring(15);
					if((input = br.readLine())!=null && startsWith(input,"QUESTION_TYPE=")){
						q.typeQ=TypeQuestion.find.ref(Long.parseLong(input.substring(14)));
						if((input = br.readLine())!=null && input.startsWith("QUESTION_TEXTE=")){
							q.texte = input.substring(15);
							q.id=Question.idNonUtilisee();
							q.position=positionQuestion;
							positionQuestion++;
							q.serie=serie;
							q.save();
							int positionReponse = 1;
							while((input = br.readLine())!=null && input.startsWith("REPONSE=")){
								Reponse r = new Reponse();
								r.position = positionReponse;
								positionReponse++;
								r.question=q;
								r.texte=input.substring(8);
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
	
}