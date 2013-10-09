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

package test;

/**
 * Pour savoir si on est en mode de test ou pas
 * On configure le mode où l'on est à la ligne 77 de conf/application.conf
 * en la changeant en :
 * test.enabled=yes
 *  ou
 * test.enabled=no
 * @author Admin
 *
 */
public class Mode {
	/**
	 * Détermine si on est en mode de test ou pas.
	 * @return VRAI ou FAUX
	 */
	public static boolean isEnabled(){
		return play.Play.application().configuration().getString("test.enabled").equals("yes");
	}
	
	/**
	 * Détermine si oui ou non on envoie les mails.
	 * @return VRAI ou FAUX
	 */
	public static boolean mailEnabled(){
		return play.Play.application().configuration().getString("mail.enabled").equals("yes");
	}
	
	/**
	 * Détermine si oui ou non on envoie les mails.
	 * @return VRAI ou FAUX
	 */
	public static boolean findAllEnabled(){
		return play.Play.application().configuration().getString("find.all").equals("yes");
	}
}
