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
}
