/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho.de.pd.directoria;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafael
 */
public class TrabalhoDePDDirectoria {

    static boolean exec = true;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
       gestorHB gest = new gestorHB();     
       
       gest.iniciar();
    }
}