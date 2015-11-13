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
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafael
 */
public final class gestorHB {
    
    private String EndIP;
    private final static String grupo = "225.15.15.15";
    
    final int timeToWait = 5000;
    final int port = 7000;
    
    private boolean exec;
    
    private MulticastSocket clientSocket;
    private boolean servidorExiste;

    public gestorHB() {
        exec = true;
        InetAddress address;
        
        try {
            address = InetAddress.getByName(grupo);
            clientSocket = new MulticastSocket(port);
            clientSocket.joinGroup(address);
        } catch (SocketException ex) {
            Logger.getLogger(gestorHB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gestorHB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void iniciar(){
        Runnable enviar = () -> {
            try {
                enviarIP();
            } catch (IOException | InterruptedException | ClassNotFoundException ex) {
                Logger.getLogger(TrabalhoDePDDirectoria.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        
        new Thread(enviar).start();
        
        Runnable receber = () -> {
            try {
                receberIP();
            } catch (UnknownHostException ex) {
                Logger.getLogger(gestorHB.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(gestorHB.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        
        new Thread(receber).start();
    }
    
    private void receberIP() throws SocketException, UnknownHostException, IOException {
        byte[] receiveData = new byte[1024];

        System.out.println("A iniciar servidor de directoria...");

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        EndIP = receivePacket.getAddress().toString();

        System.out.println("O IP do servidor primário é " + EndIP);
    }
    
    private void enviarIP() throws SocketException, IOException, InterruptedException, ClassNotFoundException{
        do{
            if (servidorExiste) {
                byte[] receiveData = new byte[1024];
                ClienteInfo temp;

                System.out.println("A procura de clientes...");

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                System.out.println("Encontrado um cliente...");

                ObjectInputStream ler = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData()));
                temp = (ClienteInfo) ler.readObject();

                System.out.println("Novo cliente, IP :" + temp.getUsername());
            }
            
            Thread.sleep(timeToWait);
        }while(exec);                                        
    }
}
