
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class DisSumWorker implements ClientRMI {
    //buscar referencia al objecte remot...
    private static MOM_interface server;
    public static void main(String args[]){
        try{
            String registry = "localhost";
            if(args.length >= 1){registry = args[0];}
            String registration = "rmi://" + registry + "/MOMServer";
// Localizar el servicio en el registro y obtener el servicio remoto.
            Remote remoteService = Naming.lookup(registration);
            server = (MOM_interface) remoteService;
// Crear un cliente y registrarlo
        }catch (NotBoundException nbe) {
            System.out.println ("No sensors available");
        } catch (RemoteException re) {
            System.out.println ("RMI Error - " + re);
        } catch (Exception e) {
            System.out.println ("Error - " + e);
        }
    }
    @Override
    public void onTopicMessage(String message) {

    }

    @Override
    public void onTopicClosed() {

    }
}
