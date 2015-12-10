/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho.de.pd.directoria;

/**
 *
 * @author ASUS
 */
public class VerificaServidores extends Thread{
    
    gestorHB gestor=null;
    Boolean running=false;
    
    public VerificaServidores(gestorHB gestor){
        this.gestor=gestor;
        running=true;
    }
    
    public void termina(){
        running=false;
    }
    
    @Override
    public void run() {
        while(running){
            for(int i=0;i<gestor.getServidores().size();i++){
                long tFinal=System.currentTimeMillis();
                if(((tFinal-gestor.getServidores().get(i).getTStart())/1000)>15){
                    gestor.getServidores().remove(i);
                }
            }
        }
    }
}
