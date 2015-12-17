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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import trabalho.de.pd.servidor.HeartBeat;


/**
 *
 * @author Rafael
 */
public final class gestorHB {
    
    HeartbeatsRecebe threadHeartbeatsRecebe=null;
    RoundRobin threadRoundRobin=null;
    RespondeCliente threadRespondeCliente=null;
    
    int roundRobin=0;
    
    private final String grupo = "225.15.15.15";
    
    final int timeToWait = 5000;
    final int port = 7000;
    
    public boolean exec;
    public boolean servidorExiste = false;
    
    private MulticastSocket multicastSocketServidor;
    private MulticastSocket multicastSocketCliente;
    
    private ArrayList<HeartBeat> servidores=new ArrayList<>();
    private HashMap<HeartBeat,Long> temposHeartBeats = new HashMap<>();

    public gestorHB() {
        exec = true;
        servidorExiste = false;
        InetAddress address;
        
        try {
            address = InetAddress.getByName(grupo);
            multicastSocketServidor = new MulticastSocket(port);
            multicastSocketServidor.setSoTimeout(5000);
            multicastSocketServidor.joinGroup(address);
            
            multicastSocketCliente = new MulticastSocket(port+1);
            multicastSocketCliente.setSoTimeout(5000);
            multicastSocketCliente.joinGroup(address);
        } catch (SocketException ex) {
            Logger.getLogger(gestorHB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gestorHB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
    public void respondeCliente(int portoUDP,InetAddress endereço,ClienteInfo cliente){
        RespondeCliente threadResponde=new RespondeCliente(portoUDP,endereço,cliente,this);
        threadResponde.start();
    }
    */
    
    public void iniciar(){
        try {
            threadHeartbeatsRecebe=new HeartbeatsRecebe(this);
            threadHeartbeatsRecebe.start();
            
            threadRespondeCliente = new RespondeCliente(this);
            threadRespondeCliente.start();
            
            threadHeartbeatsRecebe.join();
            threadRespondeCliente.acaba();
        } catch (SocketException ex) {
            Logger.getLogger(gestorHB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(gestorHB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void trataHeartBeat(long tInicial, HeartBeat hb) {
        for (int i=0;i<servidores.size();i++) {
            if (hb.equals(servidores.get(i))) {
                temposHeartBeats.put(servidores.get(i), tInicial);
                System.out.println("[GESTOR] Atualizado servidor " + hb.getEndereço().getHostAddress()
                + " Tempo: "+ tInicial);
                return;
            }
        }
        servidores.add(hb);
        temposHeartBeats.put(hb, tInicial);
        System.out.println("[GESTOR] Adicionado servidor " + hb.getEndereço().getHostAddress()
        + " Tempo: "+tInicial);
    }
    
    public void verificaServidores(long tFinal, HeartBeat hb) {
        for (int i=0;i<servidores.size();i++) {
            if (hb.equals(servidores.get(i))) {
                long resultado = tFinal-temposHeartBeats.get(servidores.get(i));
                if(resultado/1000.0>15){
                    temposHeartBeats.remove(servidores.get(i));
                    HeartBeat h = servidores.remove(i);
                    System.out.println("[GESTOR] A esquecer servidor " + h.getEndereço().getHostAddress()+":"+h.getTcpPort()+" "+h.getPrimario());
                }
            }
        }
    }
        
    public HeartBeat getRoundRobinServer() {  //acho que isto ficava melhor na thread
        if (!servidores.isEmpty()) {
            if (roundRobin < 0 || roundRobin >= servidores.size()) {
                roundRobin = servidores.size() - 1;
            }
            HeartBeat servidor = servidores.get(roundRobin);
            roundRobin++;
            if (roundRobin < 0 || roundRobin >= servidores.size()) {
                roundRobin = 0;
            }
            return servidor;
        }
        return null;
    }
    
    public MulticastSocket getMulticastSocketServidor(){
        return multicastSocketServidor;
    }
    
    public MulticastSocket getMulticastSocketCliente(){
        return multicastSocketCliente;
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
