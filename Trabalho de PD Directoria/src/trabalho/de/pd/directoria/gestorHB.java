/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho.de.pd.directoria;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import trabalho.de.pd.servidor.HeartBeat;


/**
 *
 * @author Rafael
 */
public final class gestorHB {
    
    HeartbeatsRecebe threadHeartbeatsRecebe=null;
    VerificaServidores threadVerificaServidores=null;
    RoundRobin threadRoundRobin=null;
    
    int roundRobin=0;
    
    private final String grupo = "225.15.15.15";
    
    final int timeToWait = 5000;
    final int port = 7000;
    
    public boolean exec;
    public boolean servidorExiste = false;
    
    private MulticastSocket multicastSocket;
    
    private ArrayList<HeartBeat> servidores=new ArrayList<>();

    public gestorHB() {
        exec = true;
        servidorExiste = false;
        InetAddress address;
        
        try {
            address = InetAddress.getByName(grupo);
            multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(address);
        } catch (SocketException ex) {
            Logger.getLogger(gestorHB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gestorHB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void respondeCliente(int portoUDP,InetAddress endereço,ClienteInfo cliente){
        RespondeCliente threadResponde=new RespondeCliente(portoUDP,endereço,cliente,this);
        threadResponde.start();
    }
    
    
    public void iniciar(){
        try {
            threadHeartbeatsRecebe=new HeartbeatsRecebe(this);
            threadHeartbeatsRecebe.start();
            
            threadVerificaServidores=new VerificaServidores(this);
            threadVerificaServidores.start();
            
            threadRoundRobin=new RoundRobin(this);
            threadRoundRobin.start();
            
            threadHeartbeatsRecebe.join();
        } catch (SocketException ex) {
            Logger.getLogger(gestorHB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(gestorHB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
        
    public MulticastSocket getMulticastSocket(){
        return multicastSocket;
    }
    
    public void addServidores(HeartBeat heartBeat){
        this.servidores.add(heartBeat);
    }
    
    public ArrayList<HeartBeat> getServidores(){
        return servidores;
    }
    
    public void setRoundRobin(int roundRobin){
        this.roundRobin=roundRobin;
    }
    
    public int getRoundRobin(){
        return roundRobin;
    }
}
