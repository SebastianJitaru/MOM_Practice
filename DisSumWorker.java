
/* ---------------------------------------------------------------
Práctica 1.
Código fuente: DisSumWorker.java
Grau Informàtica
Y1051960T Sebastian Jitaru.
04345214P Gabriel Daniel Bogdan Micu.
--------------------------------------------------------------- */
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DisSumWorker implements ClientRMI {
    //buscar referencia al objecte remot...
    private static MOM_interface server;

    public DisSumWorker()throws RemoteException{}

    public static void main(String args[]){
        try{
            String registry = "localhost";
            if(args.length >= 1){registry = args[0];}
            String registration = "rmi://" + registry + "/MOMServer";
// Localizar el servicio en el registro y obtener el servicio remoto.
            Remote remoteService = Naming.lookup(registration);
            server = (MOM_interface) remoteService;
// Crear un cliente y registrarlo
            DisSumWorker client = new DisSumWorker();
            ClientRMI remoteClient = (ClientRMI) UnicastRemoteObject.exportObject(client, 0);
            System.out.println("Xivato1");
            synchronized (server) {
                server.MsgQ_CreateTopic("Work", EPublishMode.ROUNDROBIN);
            }
            System.out.println("Subscribing to Work queue");
            server.MsgQ_Subscribe("Work", remoteClient);
        }catch (NotBoundException nbe) {
            System.out.println ("No sensors available");
        } catch (RemoteException re) {
            System.out.println ("RMI Error - " + re);
        } catch (Exception e) {
            System.out.println ("Error - " + e);
        }
    }
    @Override
    public void onTopicMessage(String message) throws EMOMerror, RemoteException {
        System.out.println("This is the message recieved: " + message);
        String[] parts = message.split("-");
        long lower = Integer.parseInt(parts[0]);
        long higher = Integer.parseInt(parts[1]);
        long partialResult= SumatorioMPrimos.calcularSumaPrimos(lower,higher);

        server.MsgQ_SendMessage("Results",Long.toString(partialResult),0);
    }

    @Override
    public void onTopicClosed(String topic) throws EMOMerror, RemoteException {
        System.out.println("Topic " +topic+" is closed");
    }


}
