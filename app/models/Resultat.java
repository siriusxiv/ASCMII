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

import java.util.*;

/**
 * 
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
	
	@Override
	public int compare(Resultat r1,Resultat r2){
			return (r1.question.position<r2.question.position ? -1 : (r1.question.position==r2.question.position ? 0 : 1));
	}
	
	/**
	 * Cette fonction permet de générer la page "eleveRepondu.scala.html". C'est la page où on voit les résultats
	 * de l'élève quand il a déjà répondu.
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
				resultat.distribue(listFinale);
			}
			resultats.add(resultat);
		}
		//Maintenant, il faut trier les résultats par question
		Collections.sort(resultats,new Resultat(new Question()));
		return resultats;
	}
	
	
	public void distribue(List<CoupleRI> listCRI){
		listRepond=new ArrayList<Repond>();
		reponsesChoisies=new ArrayList<Integer>();
		for(int i = 0; i<listCRI.size(); i++){
			listRepond.add(0,listCRI.get(i).repond);
			reponsesChoisies.add(0,listCRI.get(i).i);
		}
	}
}











