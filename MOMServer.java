/* ---------------------------------------------------------------
Práctica 1.
Código fuente: MOMServer.java
Grau Informàtica
Y1051960T Sebastian Jitaru.
04345214P Gabriel Daniel Bogdan Micu.
--------------------------------------------------------------- */

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static java.lang.System.exit;

public class MOMServer extends MOM_servant {

    public void MOMServer() throws RemoteException{}
    public static void main(String[] args){
        String serverIP = "localhost";
        if (args.length >= 1) {
            serverIP = args[0];
        }
        MsqQ_Init(serverIP);
    }

    private static void MsqQ_Init(String serverIP){
       try {
           System.out.println("Cargando servicios RMI. Levantando gestor de mensajes");

           MOM_servant messageManager = new MOM_servant();
           // Generamos los stubs de forma dinámica
           MOM_interface server = (MOM_interface) UnicastRemoteObject.exportObject(messageManager, 0);
           System.out.println(String.format("El gestor de mensajes se va a levantar en la IP %s", serverIP));
           Registry registry = LocateRegistry.getRegistry();
           registry.rebind("MOMServer", server);

           // Vinculamos el objeto messageManager a la ruta asignada
           System.out.println(String.format("Objeto bindeado %s", registry));
       }
       catch(RemoteException re)
       {
           System.err.println("Remote Error - " + re);
       }
       catch(Exception e)
       {
               System.err.println("Error - " + e);
       }

    }

}

