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
    public void run()  {
        System.out.println("Thread HeartbeatsRecebe a correr...");
        Object msg=null;
        boolean flg=false;
        long tInicial=0;
        do {
            try {
                msg=null;
                flg=false;
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                gestor.getMulticastSocket().receive(packet);
                ObjectInputStream recv = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                msg = (Object) recv.readObject();
                tInicial=System.currentTimeMillis();
                if(msg instanceof HeartBeat){
                    HeartBeat gH=(HeartBeat)msg;
                    for(int i=0;i<gestor.getServidores().size();i++){
                        if(gestor.getServidores().get(i).equals(gH)){
                            gestor.getServidores().get(i).setTStart(tInicial);
                            flg=true;
                            break;
                        }
                    }
                    if(flg==false){              
                        gH.setTStart(tInicial);
                        gestor.getServidores().add(gH);
                    }
                    System.out.println("[GESTOR] Received Heartbeat " + packet.getAddress().getHostAddress() + " Tipo: " + ((HeartBeat) msg).getPrimario());                   
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
            } finally { 
                if (msg instanceof HeartBeat){
                    gestor.trataHeartBeat(tInicial,(HeartBeat)msg);
                    gestor.verificaServidores(tInicial+10, (HeartBeat)msg);
                }
            }
        } while (running);
    }
}
