
import java.rmi.Remote;
import java.rmi.RemoteException;

//interficie que implementara los metdoos de callback, el tipo del objeto remoto del cliente sera tipo TopicListnerInterfaceis
public interface ClientRMI extends Remote {
  void onTopicMessage();
  void onTopicClosed();
}
