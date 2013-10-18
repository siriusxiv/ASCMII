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

public class Groupe {
	private int id;
	private String nom;

	public Groupe(int id_, String nom_){
		id=id_;
		nom=nom_;
	}
	
	/**
	 * Accessor for name
	 * @return name
	 */
	public String getName(){
		return nom;
	}
	
	/**
	 * Obtient l'ID d'une groupe
	 * @param nom_
	 * @return Si le groupe existe, renvoie son id, sinon, renvoie -1
	 */
	public static Integer getID(String nom_){
		for(Groupe g : AGAPUtil.listGroupes){
			if((g.nom).equals(nom_)){
				return g.id;
			}
		}
		return -1;
	}
	
	/**
	 * Vérifie que le groupe rentré existe
	 * @param nom_
	 * @return
	 */
	public static boolean exists(String nom_){
		for(Groupe g : AGAPUtil.listGroupes){
			if((g.nom).equals(nom_)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Fonction utilisée pour tests seulement
	 */
	public static void createList(){
		AGAPUtil.listGroupes.add(new Groupe(0,"A"));
		AGAPUtil.listGroupes.add(new Groupe(1,"B"));
		AGAPUtil.listGroupes.add(new Groupe(2,"C"));
		AGAPUtil.listGroupes.add(new Groupe(3,"D"));
		AGAPUtil.listGroupes.add(new Groupe(4,"E"));
		AGAPUtil.listGroupes.add(new Groupe(5,"F"));
		AGAPUtil.listGroupes.add(new Groupe(6,"G"));
		AGAPUtil.listGroupes.add(new Groupe(7,"H"));
		AGAPUtil.listGroupes.add(new Groupe(8,"I"));
		AGAPUtil.listGroupes.add(new Groupe(9,"J"));
		AGAPUtil.listGroupes.add(new Groupe(10,"K"));
		AGAPUtil.listGroupes.add(new Groupe(11,"L"));
		AGAPUtil.listGroupes.add(new Groupe(12,"1"));
		AGAPUtil.listGroupes.add(new Groupe(13,"2"));
		AGAPUtil.listGroupes.add(new Groupe(14,"3"));
		AGAPUtil.listGroupes.add(new Groupe(15,"4"));
		AGAPUtil.listGroupes.add(new Groupe(16,"5"));
		AGAPUtil.listGroupes.add(new Groupe(17,"6"));
		AGAPUtil.listGroupes.add(new Groupe(18,"7"));
		AGAPUtil.listGroupes.add(new Groupe(19,"8"));
		AGAPUtil.listGroupes.add(new Groupe(20,"9"));
		AGAPUtil.listGroupes.add(new Groupe(21,"10"));
		AGAPUtil.listGroupes.add(new Groupe(22,"11"));
		AGAPUtil.listGroupes.add(new Groupe(23,"12"));
		AGAPUtil.listGroupes.add(new Groupe(24,"autre"));
	}
}
