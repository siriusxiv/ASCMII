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

package functions.agap;

import functions.AGAPUtil;

/**
 * La classe contenant les matières
 * libelle : c'est le libellé de la matière, "Algorithme et programmation" par exemple
 * libellecourt : "ALGPR" par exemple
 * semestre : c'est le semestre "S5" par exemple
 * id : l'id de la matière dans AGAP
 * @author Admin
 *
 */
public class Matiere {
	public String libelle;
	public String libellecourt;
	public String semestre;
	public Integer id;
	
	/**
	 * Constructeur basique
	 * @param li
	 * @param lic
	 * @param semestre_
	 * @param id_
	 */
	public Matiere(String li,String lic,String semestre_,Integer id_){
		libelle=li;
		libellecourt=lic;
		semestre=semestre_;
		id=id_;
	}
	
	/**
	 * Constructeur simple pour tests
	 * @param libelle_court
	 * @param id_
	 */
	public Matiere(String libelle_court,int id_){
		libelle=libelle_court;
		libellecourt=libelle_court;
		semestre="";
		id=id_;
	}
	
	/**
	 * Obtient l'ID d'une matiere
	 * @param libellecourt_
	 * @return Si la marière existe, renvoie son id, sinon, renvoie -1
	 */
	public static Integer getID(String libellecourt_){
		for(Matiere m : AGAPUtil.listMatieres){
			if((m.libellecourt+m.semestre).equals(libellecourt_)){
				return m.id;
			}
		}
		return -1;
	}
	
	/**
	 * Vérifie que la matiere rentrée existe
	 * @param libellecourt_
	 * @return
	 */
	public static boolean exists(String libellecourt_){
		for(Matiere m : AGAPUtil.listMatieres){
			if((m.libellecourt+m.semestre).equals(libellecourt_)){
				return true;
			}
		}
		return false;
	}
	
	public static void createList(){
		AGAPUtil.listMatieres.add(new Matiere("ALGPR",0));
		AGAPUtil.listMatieres.add(new Matiere("MELOG",1));
		AGAPUtil.listMatieres.add(new Matiere("PRSTA",2));
		AGAPUtil.listMatieres.add(new Matiere("TEST",3));
		AGAPUtil.listMatieres.add(new Matiere("FLUID_S5",4));
		AGAPUtil.listMatieres.add(new Matiere("FLUID_S6",5));
		AGAPUtil.listMatieres.add(new Matiere("COES",6));
	}
}
