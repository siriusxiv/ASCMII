package functions.agap;

import functions.AGAPUtil;

/**
 * La classe contenant les matières
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
	 * Obtient l'ID d'une matiere
	 * @param libellecourt
	 * @return Si la marière existe, renvoie son id, sinon, renvoie -1
	 */
	public static Integer getID(String libellecourt){
		for(Matiere m : AGAPUtil.listMatieres){
			if(m.libellecourt.equals(libellecourt)){
				return m.id;
			}
		}
		return -1;
	}
}
