/* ---------------------------------------------------------------
Práctica 1.
Código fuente: DisSumMaster.java
Grau Informàtica
Y1051960T Sebastian Jitaru.
04345214P Gabriel Daniel Bogdan Micu.
--------------------------------------------------------------- */
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public class DisSumMaster {
    private static MOM_interface server;

    public DisSumMaster()throws RemoteException{}
    public static void main(String args[]) throws EMOMerror {
        if (args.length < 2) {
            System.out.println("Uso: DisSumMaster <intervalo_final_sum> <#trabajos>");
            System.exit(1);
        }
        long begin = 0;
        long end = Integer.parseInt(args[0]); //limite superior del rango a sumar
        int numProcesses = Integer.parseInt(args[1]); //Numero de tareas a generar
        long intervalSize = (end-begin) / numProcesses;
        try {
            String registry = "localhost";
            if (args.length == 3) {
                registry = args[2];
            }
// Localizar el servicio en el registro y obtener el servicio remoto.
            String registration = "rmi://" + registry + "/MOMServer";
            Remote remoteService = Naming.lookup(registration);
            server = (MOM_interface) remoteService;
// Crear las colas Result y Work
            server.MsgQ_CreateQueue("Results");
            //server.MsgQ_CreateTopic("Work", EPublishMode.ROUNDROBIN);
// Publicar mensajes a la cola Work
            publishMessages(begin, end, numProcesses, intervalSize);
// Recibir mensajes de la cola Result
            recieveMessages(numProcesses);
            closeQueues();
        } catch (NotBoundException nbe) {
            System.out.println("No sensors available");
        } catch (RemoteException re) {
            System.out.println("RMI Error - " + re);
        } catch (Exception e) {
            System.out.println("Error - " + e);
        }
    }

    private static void closeQueues() throws EMOMerror, RemoteException {
        server.MsgQ_CloseTopic("Work");
        server.MsgQ_CloseQueue("Results");
    }

    private static void recieveMessages(int numProcesses) throws EMOMerror, RemoteException, InterruptedException {
        long totalSum = 0;
        int count = 0;
        while(true){
            Thread.sleep(3000);
            String message = server.MsgQ_ReceiveMessage("Results",0);
            if (message == null){
                System.out.println("DEBUG -> Null message recieved ");
            }else{
                System.out.println("DEBUG -> Message:" + message +" recieved");
                totalSum += Integer.parseInt(message);
                count++;
            }
            if(count == numProcesses){break;}

        }
        System.out.println("Sum result is : "+totalSum);
    }

    private static void publishMessages(long begin, long end, int numProcesses, long intervalSize) throws EMOMerror, RemoteException {
        for (int i = 0; i < numProcesses; i++) {
            long intervalBegin = begin + i * intervalSize;
            long intervalEnd = (i == numProcesses - 1) ? end : intervalBegin + intervalSize;
            String msg = intervalBegin + "-" + intervalEnd;
            server.MsgQ_Publish("Work",msg,0);
        }
    }

}
