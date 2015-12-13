/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho.de.pd.directoria;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ASUS
 */
public class RoundRobin extends Thread{
    
    gestorHB gestor=null;
    Boolean running=false;
    
    public RoundRobin(gestorHB gestor){
        this.gestor=gestor;
        running=true;
    }
    
    public void termina(){
        running=false;
    }
    
    @Override
    public void run(){
        while(running){
            try {
                sleep(5000);
                if(!gestor.getServidores().isEmpty()){
                    if(gestor.getServidores().size()>gestor.getRoundRobin()+1){
                        gestor.setRoundRobin(gestor.getRoundRobin()+1);
                    }else{
                        gestor.setRoundRobin(0);
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(RoundRobin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
