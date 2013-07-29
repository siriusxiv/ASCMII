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

package functions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Pour parser les dates
 * @author malik
 *
 */
public class ParseDate {
	
	/**
	 * Parse une date selon le format français
	 * @param dateString
	 * @return la date parsée
	 */
	static Date parseFrench(String dateString){
		Date date=null;
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try{
			date=df.parse(dateString);
		} catch(ParseException e){
		}
		return date;
	}
	
	/**
	 * Renvoie la dernière seconde du temps Unix
	 * @return Date
	 */
	public static Date lastDate(){
		return parseFrench("2038/01/19 03:14:08");
	}
	
	/**
	 * Renvoie la date correspondant à l'année, le mois, le jour et l'heure donnés.
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @return Date
	 */
	public static Date parseFrench(String year, String month, String day, String hour){
		return parseFrench(year + "/" + month + "/" + day + " " + hour +":00:00");
	}
}
