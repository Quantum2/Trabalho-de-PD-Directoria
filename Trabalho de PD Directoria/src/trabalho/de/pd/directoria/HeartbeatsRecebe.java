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
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import trabalho.de.pd.servidor.HeartBeat;
import trabalho.de.pd.ClienteInfo;

/**
 *
 * @author ASUS
 */
public class HeartbeatsRecebe extends Thread{

    gestorHB gestor=null;
    public static final int MAX_SIZE=10000;
    int contador = 0;
    protected DatagramPacket packet = null;
    protected boolean running = false;
    public InetAddress ipPrimario = null;
    public int portoPrimario = 0;

    public HeartbeatsRecebe(gestorHB gestor) throws SocketException {
        this.gestor=gestor;
        running = true;
    }

    public InetAddress getIpPrimario() {
        return ipPrimario;
    }

    public int getPortoPrimario() {
        return portoPrimario;
    }

    public void termina() {
        running = false;
    }

    @Override
    public void run()  {   //falta fazer quando ha mais do que 1 primario && o fazer o tempo de 5 segundos a espera e nao com o timeout
        System.out.println("Thread HeartbeatsRecebe a correr...");
        Object msg=null;
        do {
            try {
                msg=null;
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                gestor.getMulticastSocket().receive(packet);
                ObjectInputStream recv = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                msg = (Object) recv.readObject();
                long tInicial=System.currentTimeMillis();
                if(msg instanceof HeartBeat){
                    /*
                    for(int i=0;i<gestor.getServidores().size();i++){
                        if(gestor.getServidores().get(i)==msg){
                            gestor.getServidores().get(i).setTStart(tInicial);
                            break;
                        }
                    }
                    */
                    System.out.println("[GESTOR] Received Heartbeat " + packet.getAddress().getHostAddress() + ((HeartBeat) msg).getPrimario());
                    gestor.trataHeartBeat(tInicial,(HeartBeat)msg);
                    gestor.verificaServidores(tInicial+10, (HeartBeat)msg);
                }else{
                    if(msg instanceof ClienteInfo){
                        ClienteInfo c=(ClienteInfo)msg;
                        gestor.respondeCliente(packet.getPort(),packet.getAddress(),c);
                        System.out.println("[GESTOR] Received Client Connection " + packet.getAddress().getHostAddress() + " " +((ClienteInfo)msg).getUsername()
                        + ((ClienteInfo)msg).getPassword());
                    }
                }
                
            } catch (NumberFormatException e) {
                System.out.println("O porto de escuta deve ser um inteiro positivo.");
            } catch (SocketException e) {
                System.out.println("Ocorreu um erro ao nível do socket UDP:\n\t" + e);
            } catch (SocketTimeoutException e) {

            } catch (IOException e) {
                System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(gestorHB.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (running);
    }
}
