/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho.de.pd.directoria;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import trabalho.de.pd.servidor.RMIServidorInterface;
import trabalho.de.pd.servidor.RMIInfo;

/**
 *
 * @author ASUS
 */
public class RMIListener {
    
    gestorHB gestor=null;
    
    public RMIListener(gestorHB gestor){
                
        this.gestor=gestor;
    }
    
    public void OuveRMI(String endereço) {
        try {
            String objectUrl = "rmi://127.0.0.1/RemoteTime"; //rmiregistry on localhost
            System.out.println("[RMIDIRETORIA] Endereço:"+endereço);
            objectUrl = "rmi://" + endereço + "/RMITrabalho";

            RMIServidorInterface rmiServidor = (RMIServidorInterface) Naming.lookup(objectUrl);
            RMIInfo info=(RMIInfo)rmiServidor.getInfo();
            this.gestor.addInfoRMI(info);
            System.out.println("[RMIDIRETORIA] Info obtido");
        } catch (RemoteException e) {
            System.out.println("Erro remoto - " + e);
        } catch (NotBoundException e) {
            System.out.println("Servico remoto desconhecido - " + e);
        } catch (Exception e) {
            System.out.println("Erro - " + e);
        }
    }
}
