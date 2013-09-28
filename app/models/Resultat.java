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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;





/**
 * Cette classe permet de rendre compte des résultats d'un questionnaire.
 * Elle est utilisée de deux manière complètement différentes :
 * l'une pour afficher les résultats d'un élève à une série de questions,
 * l'autre pour afficher les résultats de tous les élèves à une série de questions.
 * @author Admin
 *
 */
public class Resultat implements Comparator<Resultat>{
	public Question question;
	public Repond repond;
	public List<Integer> reponsesChoisies;
	public List<Repond> listRepond;
	public Integer nombreDeRepondants;
	
	Resultat(Question q){
		question=q;
	}
	
	/**
	 * Définit une relation d'ordre dépendant de la position des questions.
	 */
	@Override
	public int compare(Resultat r1,Resultat r2){
			return (r1.question.position<r2.question.position ? -1 : (r1.question.position==r2.question.position ? 0 : 1));
	}
	
	/**
	 * Cette fonction permet de générer la page "eleveRepondu.scala.html". C'est la page où on voit les résultats
	 * de l'élève quand il a déjà répondu.
	 * Ici :	- question contient la question à laquelle l'élève à répondu
	 * 			- repond contient le repond éventuelle (si la question est de type 3 ou 4)
	 * 			- reponsesChoisies contient la liste des réponses que l'élève à choisie pour cette question (pour les questions de type 1 ou 2)
	 * 			- listRepond et nombreDeRepondants ne servent à rien ici.
	 * @param lien
	 * @return Cette fonction renvoie la liste des réponses au question d'un certain élève pour une certaine série
	 */
	public static List<Resultat> listeResultat(Lien lien){
		Serie serie = lien.serie;
		Eleve eleve = lien.eleve;
		List<Resultat> resultats = new ArrayList<Resultat>();
		for(Question q : serie.questions){
			Resultat resultat = new Resultat(q);
			if(q.typeQ.id==1 || q.typeQ.id==2){
				List<Integer> rChoisies = new ArrayList<Integer>();
				for(Reponse r : q.reponses){
					Choisit choisit = Choisit.find.where().eq("reponse",r).eq("eleve", eleve).findUnique();
					if(choisit!=null){
						rChoisies.add(choisit.reponse.position);
					}
				}
				resultat.reponsesChoisies = rChoisies;
			}else if(q.typeQ.id==3 || q.typeQ.id==4){
				resultat.repond = Repond.find.where().eq("question", q).eq("eleve", eleve).findUnique();
			}
			resultats.add(resultat);
		}
		//Maintenant, il faut trier les résultats par question
		Collections.sort(resultats,new Resultat(new Question()));
		//Et trier les reponses aux questions :
		for(Resultat r : resultats){
			Collections.sort(r.question.reponses,new Reponse());
		}
		return resultats;
	}
	
	/**
	 * Cette fonction permet de générer les pages "resultatFin.scala.html" et "resultatEnCours.scala.html".
	 * À partir d'une série, on fait la liste de toutes ses questions, pour chaque question :
	 * 		- si la question est de type 1 ou 2 : reponsesChoisies contient le nombre de réponses pour
	 * chaque réponse en comptant tous les votants.
	 * 		- si la question est de type 3 ou 4 : listRepond (celui qui sera stocké dans les résultats)
	 * contiendra les 10 réponses les plus fréquentes. reponsesChoisies contiendra le nombre de répondants.
	 * C'est pourquoi on stocke chaque Repond et nombre de réponses comme un couple dans une liste. En effet,
	 * on ne veut pas perdre la correspondance "réponse"<->"Nombre de réponse".
	 * 		Dans tous les dasn nombreDeRepondants contient le nombre de personnes ayant répondu à la série.
	 * @param serie
	 * @return Renvoie la liste des résultats
	 */
	public static List<Resultat> listeResultat(Serie serie){
		List<Resultat> resultats = new ArrayList<Resultat>();
		for(Question q : serie.questions){
			Resultat resultat = new Resultat(q);
			if(q.typeQ.id==1 || q.typeQ.id==2){
				List<Integer> rChoisies = new ArrayList<Integer>();
				//On commence par trier les réponses dans l'ordre :
				Collections.sort(q.reponses,new Reponse());
				int nombreTotalDeRepondants=0;
				for(Reponse r : q.reponses){
					//on compte le nombre de personnes ayant répondue à la question r et on les ajout dans rChoisies.
					int nombreDeRepondantsACetteReponse = Choisit.find.where().eq("reponse",r).findList().size();
					rChoisies.add(nombreDeRepondantsACetteReponse);
					nombreTotalDeRepondants+=nombreDeRepondantsACetteReponse;
				}
				resultat.nombreDeRepondants=nombreTotalDeRepondants;
				resultat.reponsesChoisies=rChoisies;
			}else if(q.typeQ.id==3 || q.typeQ.id==4){
				//On doit trouver toutes les réponses avec leur nombre respectif dans rChoisies
				List<Repond> listRepond = Repond.find.where().eq("question", q).findList();
				List<CoupleRI> listFinale = new ArrayList<CoupleRI>();
				for(Repond r : listRepond){
					int i;
					if( (i = r.isIn(listFinale)) >= 0){//Cette réponse à déjà été vue
						//On incrémente le i-ème élément de listFinale
						listFinale.set(i, new CoupleRI(r,listFinale.get(i).i+1));
					}else{//On voit cette réponse pour la première fois
						//On l'ajoute dans listFinale (avec une seule occurance pour l'instant)
						listFinale.add(new CoupleRI(r,1));
					}
				}
				//On trie la list par ordre de réponse du plus grand au plus petit
				Collections.sort(listFinale,new CoupleRI(new Repond(),0));
				resultat.nombreDeRepondants=listRepond.size();
				//Il faut maintenant ne garder que les 10 premiers et ajouter une case 11 contenant les autres.
				if(listFinale.size()>10){
					while(listFinale.size()>10){
						listFinale.remove(0);
					}
					listFinale.add(0,new CoupleRI(listFinale,resultat.nombreDeRepondants));
				}
				resultat.distribue(listFinale);
			}
			resultats.add(resultat);
		}
		//Maintenant, il faut trier les résultats par question
		Collections.sort(resultats,new Resultat(new Question()));
		return resultats;
	}
	
	/**
	 * Comme la fonction ci-dessus mais renvoie un résultat exhaustif pour les questions de type 3 ou 4
	 * @param serie
	 * @return Renvoie la liste des résultats de manière exhaustive
	 */
	public static List<Resultat> listeResultatExhaustif(Serie serie){
		List<Resultat> resultats = new ArrayList<Resultat>();
		for(Question q : serie.questions){
			Resultat resultat = new Resultat(q);
			if(q.typeQ.id==1 || q.typeQ.id==2){
				List<Integer> rChoisies = new ArrayList<Integer>();
				//On commence par trier les réponses dans l'ordre :
				Collections.sort(q.reponses,new Reponse());
				int nombreTotalDeRepondants=0;
				for(Reponse r : q.reponses){
					//on compte le nombre de personnes ayant répondue à la question r et on les ajout dans rChoisies.
					int nombreDeRepondantsACetteReponse = Choisit.find.where().eq("reponse",r).findList().size();
					rChoisies.add(nombreDeRepondantsACetteReponse);
					nombreTotalDeRepondants+=nombreDeRepondantsACetteReponse;
				}
				resultat.nombreDeRepondants=nombreTotalDeRepondants;
				resultat.reponsesChoisies=rChoisies;
			}else if(q.typeQ.id==3 || q.typeQ.id==4){
				//On doit trouver toutes les réponses avec leur nombre respectif dans rChoisies
				List<Repond> listRepond = Repond.find.where().eq("question", q).findList();
				List<CoupleRI> listFinale = new ArrayList<CoupleRI>();
				for(Repond r : listRepond){
					int i;
					if( (i = r.isIn(listFinale)) >= 0){//Cette réponse à déjà été vue
						//On incrémente le i-ème élément de listFinale
						listFinale.set(i, new CoupleRI(r,listFinale.get(i).i+1));
					}else{//On voit cette réponse pour la première fois
						//On l'ajoute dans listFinale (avec une seule occurance pour l'instant)
						listFinale.add(new CoupleRI(r,1));
					}
				}
				//On trie la liste par ordre de réponse du plus grand au plus petit
				Collections.sort(listFinale,new CoupleRI(new Repond(),0));
				resultat.nombreDeRepondants=listRepond.size();
				resultat.distribue(listFinale);
			}
			resultats.add(resultat);
		}
		//Maintenant, il faut trier les résultats par question
		Collections.sort(resultats,new Resultat(new Question()));
		return resultats;
	}
	
	
	/**
	 * Prend les réponses et le nombre de réponse dans une liste de couples RI et
	 * les redistribue dans un objet de classe "Resultat".
	 * @param listCRI
	 */
	public void distribue(List<CoupleRI> listCRI){
		listRepond=new ArrayList<Repond>();
		reponsesChoisies=new ArrayList<Integer>();
		for(int i = 0; i<listCRI.size(); i++){
			listRepond.add(0,listCRI.get(i).repond);
			reponsesChoisies.add(0,listCRI.get(i).i);
		}
	}
	
	/**
	 * Renvoie la liste exhaustive des réponses à une question de type 3 ou 4, sur le modèle
	 * de listeResultat(Serie serie), mais sans tronquer la liste au-delà de 10 réponses.
	 * @param question_id
	 * @return liste exhaustive des réponses
	 */
	public static Resultat exhaustive(Long question_id){
		Question q = Question.find.ref(question_id);
		Resultat resultat = new Resultat(q);
		List<Repond> listRepond = Repond.find.where().eq("question", q).findList();
		List<CoupleRI> listFinale = new ArrayList<CoupleRI>();
		for(Repond r : listRepond){
			int i;
			if( (i = r.isIn(listFinale)) >= 0){//Cette réponse à déjà été vue
				//On incrémente le i-ème élément de listFinale
				listFinale.set(i, new CoupleRI(r,listFinale.get(i).i+1));
			}else{//On voit cette réponse pour la première fois
				//On l'ajoute dans listFinale (avec une seule occurance pour l'instant)
				listFinale.add(new CoupleRI(r,1));
			}
		}
		//On trie la list par ordre de réponse du plus grand au plus petit
		Collections.sort(listFinale,new CoupleRI(new Repond(),0));
		resultat.nombreDeRepondants=listRepond.size();
		resultat.distribue(listFinale);
		return resultat;
	}
}

