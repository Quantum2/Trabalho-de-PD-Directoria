/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho.de.pd.directoria;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import trabalho.de.pd.ClienteInfo;
import static trabalho.de.pd.directoria.HeartbeatsRecebe.MAX_SIZE;
import trabalho.de.pd.servidor.HeartBeat;

/**
 *
 * @author ASUS
 */
public class RespondeCliente extends Thread{
    
    InetAddress endereço=null;
    int porto=0;
    DatagramSocket socket=null;
    DatagramPacket packet=null;
    ClienteInfo cliente=null;
    gestorHB gestor=null;
    boolean continuar=true;
    
    public RespondeCliente(gestorHB gestor){
        this.gestor=gestor;
        
        try {
            socket=new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(RespondeCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void acaba() {
        continuar=false;
    }
    
    @Override
    public void run(){
        do {
            try {
                ClienteInfo cliente=null;
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                gestor.getDatagramSocket().receive(packet);
                endereço=packet.getAddress();
                porto=packet.getPort();
                ObjectInputStream recv = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                cliente = (ClienteInfo) recv.readObject();
                
                System.out.println("[GESTOR] Received Client Connection " + packet.getAddress().getHostAddress() + " " + packet.getPort() + " "
                        + cliente.getUsername() + cliente.getPassword());

                String linha;
                boolean flg = false;

                FileReader file = new FileReader("C:\\Users\\Carlos Oliveira\\Desktop\\Testes Trabalho PD\\UsernamesPasswords.txt");
                BufferedReader br = new BufferedReader(file);

                while ((linha = br.readLine()) != null) {
                    String[] temp = linha.split(" ");
                    if (temp[0].equalsIgnoreCase(cliente.getUsername()) && temp[1].equalsIgnoreCase(cliente.getPassword())) {
                        flg = true;
                    }
                }
                if (flg) {
                    ByteArrayOutputStream byteout = new ByteArrayOutputStream();
                    ObjectOutputStream send = new ObjectOutputStream(byteout);
                    HeartBeat hAux = gestor.getRoundRobinServer();
                    send.writeObject(hAux);
                    send.flush();

                    packet = new DatagramPacket(byteout.toByteArray(), byteout.size(), endereço, porto);
                    socket.send(packet);
                } else {
                    System.out.println("[GESTOR] Dados login errados");
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(RespondeCliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                System.out.println("[GESTOR] RespondeCliente Timout");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(RespondeCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (continuar);
        socket.close();
    }
}
