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

/*
Je veux : la liste des "répond" fait par tel élève pour telle question
et des "choisit"

Objet{
	Répond
	Question
}
Objet{
	List<Choisit>
	Question
}

Objet{
	Répond
	List<Choisit>
	question
}
question.typeQ.id donne le type de la réponse
 */

package models;

import java.util.*;

public class Resultat implements Comparator<Resultat>{
	public Question question;
	public Repond repond;
	public List<Integer> reponsesChoisies;
	
	Resultat(Question q){
		question=q;
	}
	
	@Override
	public int compare(Resultat r1,Resultat r2){
			return (r1.question.position<r2.question.position ? -1 : (r1.question.position==r2.question.position ? 0 : 1));
	}
	
	
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
}














