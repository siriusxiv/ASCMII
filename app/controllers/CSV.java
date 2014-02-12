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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;





import models.Resultat;
import models.Seance;
import models.Serie;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Contient les fonctions relatives à l'export au format CSV
 * On peut choisir quels sont les séparateurs, les caractères marquant la fin de ligne
 * et comment sont entourés les chaînes de caractères.
 * J'ai choisi ';', "\r\n" et """ car ce sont ceux qui marchaient le mieux avec ma version
 * d'Excel. On peut remplacer """ par "", mais dans ce cas, si l'utilisateur met des séparateurs
 * (ici des ';' dans une phrase), ça ne marchera pas comme prévu.
 * Si l'utilisateur utilise des quotes, alors, l'affichage ne se déroulera non plus pas comme prévu.
 * C'est pourquoi je supprime manuellement les quotes, en les remplaçant par replacementChar (ici '_').
 * @author Admin
 *
 */
public class CSV extends Controller{
	//On fixe le séparateur :
	char separator = ';';
	//On fixe les caractères de fin de ligne :
	String lineEnd = "\r\n";
	//On indique ce qui marque le début et la fin d'une chaîne.
	String quotes = "\"";
	char replacementChar = '_';
	
	public static Result CSVdownload(Long seance_id){
		FileWriter outputStream = null;
		Seance seance = Seance.find.ref(seance_id);
		String file_name = seance_id+".csv";
		try{
			outputStream = new FileWriter(file_name);
			
			String page = new CSV().CSVgenerator(seance);
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
		response().setHeader("Content-Disposition", "attachment; filename="+Export.noSpace(seance.intitule)+".csv");
		return ok(fileIS);
	}
	
	
	/**
	 * Génère la chaîne de caractère qui sera dans le fichier de la façon définie dans le fichier CSV Template.xlsx
	 * @param seance
	 * @return
	 */
	String CSVgenerator(Seance seance){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy à H ");
		String date = df.format(seance.date)+"h";
		String s = CSVlign(seance.intitule,seance.matiere,date,"","","");
		s+=CSVlign("","","","","","");
		s+=CSVlign("Nom de la série","Question","Type de question","Réponse","Nombre de réponses","Pourcentage");
		Collections.sort(seance.series,new Serie());
		for(Serie serie : seance.series){
			List<Resultat> resultats = Resultat.listeResultatExhaustif(serie);
			boolean premiereLigneDeLaSerie=true;
			for(Resultat resultat : resultats){
				boolean premiereLigneDeLaQuestion=true;
				if(resultat.question.typeQ.id==1 || resultat.question.typeQ.id==2){
					for(int i = 0; i<resultat.question.reponses.size();i++){
						if(premiereLigneDeLaQuestion && premiereLigneDeLaSerie){
							Integer nombreDeReponses = resultat.reponsesChoisies.get(i);
							s+=CSVlign(serie.nom,resultat.question.titre,resultat.question.typeQ.typeQ,resultat.question.reponses.get(i).texte,nombreDeReponses.toString(),proportionCalcul(nombreDeReponses,resultat.nombreDeRepondants));
							premiereLigneDeLaQuestion=false;
							premiereLigneDeLaSerie=false;
						}else if(premiereLigneDeLaQuestion && !premiereLigneDeLaSerie){
							Integer nombreDeReponses = resultat.reponsesChoisies.get(i);
							s+=CSVlign("",resultat.question.titre,resultat.question.typeQ.typeQ,resultat.question.reponses.get(i).texte,nombreDeReponses.toString(),proportionCalcul(nombreDeReponses,resultat.nombreDeRepondants));
							premiereLigneDeLaQuestion=false;
						}else{
							Integer nombreDeReponses = resultat.reponsesChoisies.get(i);
							s+=CSVlign("","","",resultat.question.reponses.get(i).texte,nombreDeReponses.toString(),proportionCalcul(nombreDeReponses,resultat.nombreDeRepondants));
						}
					}
				}else if(resultat.question.typeQ.id==3 || resultat.question.typeQ.id==4){
					if(resultat.listRepond.isEmpty()){//Ici, on est forcément à la première ligne de la question.
						if(premiereLigneDeLaSerie){
							s+=CSVlign(serie.nom,resultat.question.titre,resultat.question.typeQ.typeQ,"Aucune réponse n'a été donnée par les élèves...","","");
							premiereLigneDeLaSerie=false;
						}else{
							s+=CSVlign("",resultat.question.titre,resultat.question.typeQ.typeQ,"Aucune réponse n'a été donnée par les élèves...","","");
						}
						premiereLigneDeLaQuestion=false;
					}else{
						for(int i = 0; i<resultat.listRepond.size();i++){
							if(premiereLigneDeLaQuestion && premiereLigneDeLaSerie){
								Integer nombreDeReponses = resultat.reponsesChoisies.get(i);
								s+=CSVlign(serie.nom,resultat.question.titre,resultat.question.typeQ.typeQ,resultat.listRepond.get(i).texte,nombreDeReponses.toString(),proportionCalcul(nombreDeReponses,resultat.nombreDeRepondants));
								premiereLigneDeLaQuestion=false;
								premiereLigneDeLaSerie=false;
							}else if(premiereLigneDeLaQuestion && !premiereLigneDeLaSerie){
								Integer nombreDeReponses = resultat.reponsesChoisies.get(i);
								s+=CSVlign("",resultat.question.titre,resultat.question.typeQ.typeQ,resultat.listRepond.get(i).texte,nombreDeReponses.toString(),proportionCalcul(nombreDeReponses,resultat.nombreDeRepondants));
								premiereLigneDeLaQuestion=false;
							}else{
								Integer nombreDeReponses = resultat.reponsesChoisies.get(i);
								s+=CSVlign("","","",resultat.listRepond.get(i).texte,nombreDeReponses.toString(),proportionCalcul(nombreDeReponses,resultat.nombreDeRepondants));
							}
						}
					}
				}
			}
		}
		return s;
	}
	
	/**
	 * Permet de générer une ligne d'un fichier CSV.
	 * On peut mettre autant d'arguments qu'on le souhait. Cette fonction concaténera les chaînes
	 * en mettant le caractère de séparation au milieu et des quotes.
	 * Par exemple, "CSVlign(pika,chu);"
	 * 			 renvoie :
	 * 			"\"pika\";\"chu\""
	 * @param strings : autant de string que l'on veut en argument, que l'on va concaténer.
	 * @return la string qui compose une ligne du ficher CSV
	 */
	String CSVlign(String... strings){
		String s = "";
		for(int i = 0; i<strings.length-1 ; i++){
			s+=quotes+removeQuotes(strings[i])+quotes+separator;
		}
		s+=quotes+removeQuotes(strings[strings.length-1])+quotes+lineEnd;
		return s;
	}
	
	/**
	 * Remplace les quotes dans la string par le replacementChar.
	 * Si aucune quote n'est définie, alors ne fait rien.
	 * @param s : une string quelconque
	 * @return la string modifiée.
	 */
	String removeQuotes(String s){
		if(quotes.isEmpty()){
			return s;
		}else{
			return s.replace(quotes.charAt(0), replacementChar);
		}
	}
	
	/**
	 * Fait juste un tout petit calcul de calcul de pourcentage et renvoie une chaîne.
	 * @param nombreDeReponses
	 * @param nombreDeRepondants
	 * @return le poucentage ici du calcul avec %.
	 */
	static String proportionCalcul(int nombreDeReponses, int nombreDeRepondants){
		if(nombreDeRepondants==0){
			return "0%";
		}else{
			return (nombreDeReponses*100/nombreDeRepondants)+"%";
		}
	}
}
