/**
 * MD5Helper
 *
 * Copyright (C) 2012 Sh1fT
 *
 * This file is part of Serveur_Chat_Holidays.
 *
 * Serveur_Chat_Holidays is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * Serveur_Chat_Holidays is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Serveur_Chat_Holidays; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Manage a {@link MD5Helper}
 * @author Sh1fT
 */
public class MD5Helper {
    /**
     * Encode la chaîne passée en paramètre avec l'algorithme MD5
     * @param key
     * @return String
     */
    public static String getEncodedPassword(String key) {
        byte[] uniqueKey = key.getBytes();
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("MD5").digest(uniqueKey);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Aïe Aïe Aïe !\n" + ex.getLocalizedMessage());
            System.exit(1);
        }
        StringBuilder hashString = new StringBuilder();
        for (int i = 0; i < hash.length; ++i) {
            String hex = Integer.toHexString(hash[i]);
            if (hex.length() == 1) {
                hashString.append('0');
                hashString.append(hex.charAt(hex.length()-1));
            } else
                hashString.append(hex.substring(hex.length()-2));
        }
        return hashString.toString();
    }

    /**
     * Test une chaîne et une valeur encodée (chaîne hexadécimale)
     * @param clearTextTestPassword
     * @param encodedActualPassword
     * @return Boolean
     */
    public static Boolean testPassword(String clearTextTestPassword,
        String encodedActualPassword) {
	return getEncodedPassword(clearTextTestPassword).equals(encodedActualPassword);
    }

    public static void main(String[] args) {
        System.out.println(getEncodedPassword("mot de passe"));
        if (testPassword("mot de passe", "729f2d8b3d3d9bc07ba349faab7fdf44"))
            System.out.println("Les passwords sont vérifiés");
    }
}