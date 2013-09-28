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

package alea;

public class Alea {
	
	/**
	 * Returns a random integer between a and b included (a<=b)
	 * @param a
	 * @param b
	 * @return a random integer
	 */
	public static int randomIntBetween(int a, int b){
		double r = Math.random();
		return (int) (r*(b+1-a)+a);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String nullOrNot(){
		double r = Math.random();
		if(r<0.5)	return null;
		else		return "";
	}
}
