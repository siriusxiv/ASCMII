package models;
import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.data.validation.Constraints.*;

@Entity
public class TypeQuestion extends Model{
	@Id
	public Long id;
	
	@Required
	public String type;
	
	public static Finder<Long,TypeQuestion> find = new Finder<Long,TypeQuestion>(Long.class,TypeQuestion.class);
	
	public static List<TypeQuestion> page(){
		return find.all();
	}
	
	public static void addType(TypeQuestion typeQ){
		typeQ.save();
	}
	
	public static void removeType(Long id){
		TypeQuestion typeQ = TypeQuestion.find.ref(id);
		if(typeQ != null){
			typeQ.delete();
		}
	}
	
}
