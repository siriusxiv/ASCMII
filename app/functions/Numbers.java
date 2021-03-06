/****************************************************
 * Merci à l'anonyme qui a posté cette fonction isDouble
 * sur StackOverflow, j'ai juste rajouté la première
 * ligne pour tenir compte de la virgule française. 
 * 
 ****************************************************/

package functions;

/**
 * Contient quelques fonctions qui sont utiles pour vérifier quelques entrées.
 * @author Admin
 *
 */
public class Numbers{

	/**
	 * Vérifie que la chaine "value" est un nombre (entier, à virgule ',', à virgule '.'
	 * ou même en notation exponentielle '5e-10').
	 * @param value : chaine à vérifier
	 * @return vrai ou faux
	 */
	public static boolean isDouble(String value)
	{
		value = value.replace(',','.');
		boolean seenDot = false;
		boolean seenExp = false;
		boolean justSeenExp = false;
		boolean seenDigit = false;
		for (int i=0; i < value.length(); i++)
		{
			char c = value.charAt(i);
			if (c >= '0' && c <= '9')
			{
				seenDigit = true;
				continue;
			}
			if ((c == '-' || c=='+') && (i == 0 || justSeenExp))
			{
				continue;
			}
			if (c == '.' && !seenDot)
			{
				seenDot = true;
				continue;
			}
			justSeenExp = false;
			if ((c == 'e' || c == 'E') && !seenExp)
			{
				seenExp = true;
				justSeenExp = true;
				continue;
			}
			return false;
		}
		if (!seenDigit)
		{
			return false;
		}
		try
		{
			Double.parseDouble(value);
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	/**
	 * Vérifie que la chaine "value" est un nombre entier
	 * @param value
	 * @return VRAI ou FAUX
	 */
	public static boolean isInt(String value)
	{
		try
		{
			Integer.parseInt(value);
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}
	
	/**
	 * Returns a random int
	 * Useful in Lien to generate the chemin.
	 * @return
	 */
	public static int randomInt(){
		double a = Math.random();
		return (int) (a*2147483647);
	}

}