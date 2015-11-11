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

    static final int port = 7000;
    static final int timeToWait = 5000;
    static String EndIP;
    static boolean exec = true;
    
    /**
     * @param args the command line arguments
     * @throws java.net.SocketException
     * @throws java.net.UnknownHostException
     * @throws java.lang.InterruptedException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws UnknownHostException, IOException, SocketException, InterruptedException, ClassNotFoundException{
        
        Runnable r = () -> {
            try {
                enviarIP();
            } catch (IOException | InterruptedException | ClassNotFoundException ex) {
                Logger.getLogger(TrabalhoDePDDirectoria.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        
        new Thread(r).start();
        
        receberIP();
    }
    
    public static void receberIP() throws SocketException, UnknownHostException, IOException{      
        byte[] receiveData = new byte[1024];
        
        System.out.println("A iniciar servidor de directoria...");

        try (DatagramSocket clientSocket = new DatagramSocket(port)) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            
            EndIP = receivePacket.getAddress().toString();
        }
        
        System.out.println("O IP do servidor primário é " + EndIP);
    }
    
    public static void enviarIP() throws SocketException, IOException, InterruptedException, ClassNotFoundException{
        do{
            byte[] receiveData = new byte[1024];
            ClienteInfo temp;
            
            System.out.println("A procura de clientes...");
            
            DatagramSocket clientSocket = new DatagramSocket(port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);    
            
            System.out.println("Encontrado um cliente...");
            
            ObjectInputStream ler = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData()));
            temp = (ClienteInfo) ler.readObject();
            
            System.out.println("Novo cliente, IP :" + temp.getUsername());
            
            Thread.sleep(timeToWait);
        }while(exec);                                        
    }

}
