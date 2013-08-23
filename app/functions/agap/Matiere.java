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
}
