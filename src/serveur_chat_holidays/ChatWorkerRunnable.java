/**
 * ChatWorkerRunnable
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

package serveur_chat_holidays;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
import protocols.ProtocolFECOP;

/**
 * Manage a {@link ChatWorkerRunnable}
 * @author Sh1fT
 */
public class ChatWorkerRunnable implements Runnable {
    private Serveur_Chat_Holidays parent;
    private Socket cSocket;

    /**
     * Create a new {@link ChatWorkerRunnable} instance
     * @param parent
     * @param cSocket 
     */
    public ChatWorkerRunnable(Serveur_Chat_Holidays parent, Socket cSocket) {
        this.setParent(parent);
        this.setcSocket(cSocket);
    }

    public Serveur_Chat_Holidays getParent() {
        return parent;
    }

    public void setParent(Serveur_Chat_Holidays parent) {
        this.parent = parent;
    }

    public Socket getcSocket() {
        return cSocket;
    }

    public void setcSocket(Socket cSocket) {
        this.cSocket = cSocket;
    }

    public void run() {
        try {
            this.getParent().getClientLabel().setText(
                    this.getcSocket().getInetAddress().getHostAddress());
            DataInputStream dis = new DataInputStream(
                    new BufferedInputStream(this.getcSocket().getInputStream()));
            DataOutputStream dos = new DataOutputStream(
                    new BufferedOutputStream(this.getcSocket().getOutputStream()));
            StringBuilder info = new StringBuilder();
            info.setLength(0);
            byte b = 0;
            while ((b=dis.readByte()) != (byte) '\n') {
                if (b != '\n')
                    info.append((char) b);
            }
            StringTokenizer st  = new StringTokenizer(info.toString(), ";");
            String cmd = st.nextToken();
            String message = null;
            if (cmd.compareTo("LOGIN") == 0) {
                String username = st.nextToken();
                String password = st.nextToken();
                switch (this.getParent().getProtocolFECOP().login(username, password)) {
                    case ProtocolFECOP.RESPONSE_OK:
                        message = this.getParent().getGroupHost() + ":" +
                            this.getParent().getGroupPort().toString();
                        this.getParent().getProtocolFECOP().getNicks().add(username);
                        break;
                    case ProtocolFECOP.RESPONSE_KO:
                        message = "ko";
                        break;
                }
                dos.write((message + "\n").getBytes());
            } else if (cmd.compareTo("GETNICKS") == 0) {
                String nicks = "";
                for (String nick : this.getParent().getProtocolFECOP().getNicks())
                    nicks += nick + ":";
                dos.write((nicks + "\n").getBytes());
            } else if (cmd.compareTo("GETNICK") == 0) {
                String nick = st.nextToken();
                dos.write((String.valueOf(this.getParent().getProtocolFECOP().
                    getNicks().contains(nick)) + "\n").getBytes());
            } else if (cmd.compareTo("DELNICK") == 0) {
                String nick = st.nextToken();
                this.getParent().getProtocolFECOP().getNicks().remove(nick);
            }
            dos.flush();
            dos.close();
            dis.close();
            this.getcSocket().close();
            this.getParent().getClientLabel().setText("aucun");
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }
}