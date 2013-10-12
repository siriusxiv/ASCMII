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
package admin;

import java.io.File;

/**
 * Contain methods to clean the server
 * @author malik
 *
 */
public class Clean{
	/**
	 * Delete temp files the user can download
	 * @return
	 */
	public static int delete(){
		return deleteFiles("csv","seriexport","seancexport");
	}

	private static int deleteFiles(String... extensions){
		File dir = new File(play.Play.application().path().getAbsolutePath());
		File[] files = dir.listFiles();
		int counter = 0;
		for(int i = 0; i<files.length; i++){
			for(int j = 0; j<extensions.length ; j++){
				if(files[i].isFile() && files[i].getName().endsWith(extensions[j])){
					files[i].delete();
					counter++;
				}
			}
		}
		return counter;
	}
}