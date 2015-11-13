/**
 * ProtocolFECOP
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

package protocols;

import beans.BeanDBAccessMySQL;
import helpers.MD5Helper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import serveur_chat_holidays.Serveur_Chat_Holidays;

/**
 * Manage a {@link ProtocolFECOP}
 * @author Sh1fT
 */
public class ProtocolFECOP implements interfaces.ProtocolFECOP {
    private Serveur_Chat_Holidays parent;
    private BeanDBAccessMySQL bdbam;
    private List<String> nicks;
    public static final int RESPONSE_OK = 100;
    public static final int RESPONSE_KO = 600;

    /**
     * Create a new {@link Serveur_Chat_Holidays} instance
     * @param parent 
     */
    public ProtocolFECOP(Serveur_Chat_Holidays parent) {
        this.setParent(parent);
        this.setBdbam(new BeanDBAccessMySQL(
                System.getProperty("file.separator") +"properties" +
                System.getProperty("file.separator") + "BeanDBAccessMySQL.properties"));
        this.setNicks(new LinkedList<String>());
    }

    public Serveur_Chat_Holidays getParent() {
        return this.parent;
    }

    public void setParent(Serveur_Chat_Holidays parent) {
        this.parent = parent;
    }

    public BeanDBAccessMySQL getBdbam() {
        return bdbam;
    }

    public void setBdbam(BeanDBAccessMySQL bdbam) {
        this.bdbam = bdbam;
    }

    public List<String> getNicks() {
        return this.nicks;
    }

    public void setNicks(List<String> nicks) {
        this.nicks = nicks;
    }

    /**
     * Un agent veut se joindre au groupe
     * @param name
     * @param password
     * @return 
     */
    public Integer login(String name, String password) {
        try {
            String query = "SELECT * FROM gestionnairemotels WHERE nom LIKE ? " +
                "AND password LIKE ?;";
            PreparedStatement ps = this.getBdbam().getDBConnection().prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, MD5Helper.getEncodedPassword(password));
            ResultSet rs = this.getBdbam().executeQuery(ps);
            if (rs.next())
                return ProtocolFECOP.RESPONSE_OK;
            return ProtocolFECOP.RESPONSE_KO;
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            this.getBdbam().stop();
            System.exit(1);
        }
        return null;
    }
}