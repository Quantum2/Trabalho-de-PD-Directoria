/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho.de.pd.directoria;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ASUS
 */
public class RespondeCliente extends Thread{
    
    int portoUDP=0;
    InetAddress endereço=null;
    DatagramSocket socket=null;
    DatagramPacket packet=null;
    ClienteInfo cliente=null;
    gestorHB gestor=null;
    
    public RespondeCliente(int portoUDP,InetAddress endereço,ClienteInfo cliente,gestorHB gestor){
        this.portoUDP=portoUDP;
        this.endereço=endereço;
        this.cliente=cliente;
        this.gestor=gestor;
        
        try {
            socket=new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(RespondeCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run(){
        try {
            String linha;
            boolean flg=false;
            
            FileReader file=new FileReader("C:\\Users\\ASUS\\Desktop\\Trabalho-de-PD-Directoria\\Trabalho de PD Directoria\\Clientes\\Clientes.txt");
            BufferedReader br=new BufferedReader(file);
            
            while((linha=br.readLine())!=null){
                String [] temp=linha.split(" ");
                if(temp[0].equalsIgnoreCase(cliente.getUsername()) && temp[1].equalsIgnoreCase(cliente.getPassword())){
                    flg=true;
                }
            }
            if(flg){
                ByteArrayOutputStream byteout=new ByteArrayOutputStream();
                ObjectOutputStream send = new ObjectOutputStream(byteout);
                send.writeObject(gestor.getServidores().get(gestor.getRoundRobin()));
                send.flush();
                
                packet=new DatagramPacket(byteout.toByteArray(),byteout.size(),endereço,portoUDP);
                socket.send(packet);
            }else{
                //um mesg qualquer a dizer que nao foi encontrado o user ou a pass esta mal
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RespondeCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RespondeCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
