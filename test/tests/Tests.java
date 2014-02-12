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
package tests;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import alea.Alea;
import functions.Numbers;
import models.Choisit;
import models.Eleve;
import models.Lien;
import models.Question;
import models.Repond;
import models.Reponse;
import models.Resultat;
import models.Serie;

public class Tests {

    public static void testResultatSeries() {
		List<Serie> series = Serie.find.orderBy("id").findList();
		for(Serie serie : series){
			List<Resultat> resultats = Resultat.listeResultat(serie);
			assertThat(resultats.size()).isEqualTo(serie.questions.size());
			for(Resultat resultat : resultats){
				if(resultat.question.typeQ.id<=2){
					for(int a : resultat.reponsesChoisies){
						assertThat(a).isGreaterThanOrEqualTo(0);
					}
				}else if(resultat.question.typeQ.id>2){
					assertThat(resultat.listRepond.size()).isEqualTo(10);
				}				
			}
		}
    }
    
    /**
     * Test de charge pour vérifier que le serveur tient le coup même si 100
     * élèves répondent au même instant.
     */
    public static void hundredAnswerTest(){
    	Serie serie = Serie.find.byId(1L);
    	//On ajoute plein de liens pour faire tourner la boucle for très longtemps
    	int lienSize = Lien.find.all().size();
    	if(lienSize<101){
    		for(int i = 0;i<100;i++){
    			new Lien(serie,Eleve.find.byId("mboussej")).save();
    		}
    		lienSize+=100;
    	}
    	//starting test
    	System.out.println("Starting test with "+lienSize+" liens...");
    	Calendar nowBegin = Calendar.getInstance();
    	for(Lien lien : Lien.find.where().eq("serie",serie).findList()){
    		HashMap<String,String> info = new HashMap<String,String>();
    		for(Question qu : lien.serie.questions){
    			String value = null;
    			switch(qu.typeQ.id){
    			case 1:
    				value = new Integer(Alea.randomIntBetween(1, qu.reponses.size())).toString();
    				info.put("choixReponse"+qu.id, value);
    				break;
    			case 2:
    				for(Reponse r : qu.reponses){
    					value = Alea.nullOrNot(); 
    					info.put("choixReponse"+qu.id+"."+r.position, value);
    				}
    				break;
    			case 3:
    				value=qu.id+" "+lien.eleve.uid;
    				info.put("texteRepondu"+qu.id, value);
    				break;
    			case 4:
    				value=qu.id.toString();
    				info.put("nombreRepondu"+qu.id, value);
    				break;
    			default:
    			}
    		}
    		donneReponse(lien.chemin,info);
    	}
    	Calendar nowEnd = Calendar.getInstance();
    	System.out.println("Test successfully finished. It took "+nowEnd.compareTo(nowBegin)+"ms.");
    }
    
    private static boolean donneReponse(String chemin, HashMap<String,String> info){
		Lien lien = Lien.find.ref(chemin);
    	if(lien.repondu){
			return false;
		}
		//D'abord, il faut vérifier que toutes les réponses sont valides :
		for(Question qu : lien.serie.questions){
			switch(qu.typeQ.id){
			case 1:
				if(info.get("choixReponse"+qu.id)==null){
					return false;
				}
				break;
			case 2:
				boolean bool=false;
				for(Reponse r : qu.reponses){
					if(bool = info.get("choixReponse"+qu.id+"."+r.position)!=null){
						break;
					}
				}
				if(!bool){
					return false;
				}
				break;
			case 3:
				if(info.get("texteRepondu"+qu.id)==""){
					return false;
				}
				break;
			case 4:
				String nombre = info.get("nombreRepondu"+qu.id);
				if(nombre=="" || !Numbers.isDouble(nombre)){
					return false;
				}
				break;
			default:
				System.out.println("mauvais type de question : " + qu.typeQ.id);
			}
		}
		//Maintenant qu'on a vérifié les réponses, on les met dans la base de donnée
		for(Question q : lien.serie.questions){
			switch(q.typeQ.id){
			case 1:
				Choisit choix = new Choisit();
				choix.date=Calendar.getInstance().getTime();
				choix.eleve=lien.eleve;
				List<Reponse> reponses = q.reponses;
				int position = Integer.parseInt(info.get("choixReponse"+q.id));
				for(Reponse r : reponses){
					if(r.position==position){
						choix.reponse=r;
					}
				}
				choix.save();
				break;
			case 2:
				List<Reponse> reponses2 = q.reponses;
				for(Reponse r : reponses2){
					String str = info.get("choixReponse"+q.id+"."+r.position);
					if(str!=null){
						Choisit choix2 = new Choisit();
						choix2.date=Calendar.getInstance().getTime();
						choix2.eleve=lien.eleve;
						choix2.reponse=r;
						choix2.save();
					}
				}
				break;
			case 3:
				String ceQuIlARepondu = info.get("texteRepondu"+q.id);
				Repond repond = new Repond();
				repond.date=Calendar.getInstance().getTime();
				repond.eleve=lien.eleve;
				repond.question=q;
				repond.texte=ceQuIlARepondu;
				repond.save();
				break;
			case 4:
				String ceQuIlARepondu2 = info.get("nombreRepondu"+q.id);
				Repond repond2 = new Repond();
				repond2.date=Calendar.getInstance().getTime();
				repond2.eleve=lien.eleve;
				repond2.question=q;
				repond2.texte=ceQuIlARepondu2.replace('.', ',');
				repond2.save();
				break;
			default:
				System.out.println("Mauvais type de question : " + q.typeQ.id);
			}
		}
		lien.repondu=true;
		lien.save();
		return true;
    }
}
