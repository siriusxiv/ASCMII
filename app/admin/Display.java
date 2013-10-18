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

import java.util.List;

import models.Image;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.admin;

/**
 * For admin purposes
 * @author malik
 *
 */
public class Display extends Controller{
	/**
	 * display admin page
	 * @return
	 */
	public static Result main(){
		return ok(admin.render());
	}
	
	/**
	 * Removes useless temp files
	 * @return how many files were deleted
	 */
	public static Result clean(){
		if(session().containsKey("admin")){
			int deletedCount = Clean.delete();
			return ok(String.valueOf(deletedCount));
		}else{
			return unauthorized();
		}
	}

	/**
	 * Get all images in the database
	 * @return
	 */
	public static Result getImages(){
		List<Image> images = Image.find.all();
		String res = "<ol>";
		for(Image i : images){
			res+="<li><img class=\"image\" src=\"/images/"+i.fileName+"\"></li>";
		}
		return ok(res+"</ol>");
	}
}
