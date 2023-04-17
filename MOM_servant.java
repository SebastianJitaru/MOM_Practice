/* ---------------------------------------------------------------
Práctica 1.
Código fuente: MOM_servant.java
Grau Informàtica
Y1051960T Sebastian Jitaru.
04345214P Gabriel Daniel Bogdan Micu.
--------------------------------------------------------------- */
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Vector;

public class MOM_servant implements MOM_interface{
    private Vector<MessageQueue> PointToPointQueues = new Vector<MessageQueue>();//una cola punto a punto y una cola topic si pueden tener el mismo nombre
    private Vector<MessageQueue> TopicQueues = new Vector<MessageQueue>();

    private MessageQueue LOG = new MessageQueue("LOG", EPublishMode.BROADCAST);
    @Override
    public void MsgQ_CreateQueue(String msgqName) throws EMOMerror, RemoteException {
        //Se comprueba si ya existe una cola con el nombre introducido
        //System.out.println("XIVATO2");
        boolean found = false;
        for (MessageQueue queue : PointToPointQueues) {
            if (queue.getName().equals(msgqName)) {
                found = true;
                break;
            }
        }
        if (found) {
            System.out.println("DEBUG -> Queue " + PointToPointQueues.get(0).getName() + " already exists");
        }else{
        //Si no existe la cola, se crea una nueva y se añade al vector que monitoriza todas las colas
            MessageQueue queue = new MessageQueue(msgqName);
            PointToPointQueues.add(queue);
            System.out.println("DEBUG -> Queue " + PointToPointQueues.get(0).getName() +" created");

        }
    }

    @Override
    public void MsgQ_CloseQueue(String topicName) throws EMOMerror, RemoteException {
        Iterator<MessageQueue> iterator = PointToPointQueues.iterator();
        while (iterator.hasNext()) {
            MessageQueue queue = iterator.next();
            if (queue.getName().equals(topicName)) {
                if (queue.isEmpty()) {
                    iterator.remove(); // Safely remove the element from the collection
                    System.out.println("DEBUG -> Topic " + topicName + " closed");
                } else {
                    System.out.println("Cannot close queue " + topicName + " because it still has pending messages.");
                }
                return; // Exit the loop after removing the element
            }
        }
        System.out.println("Queue named " + topicName + " not found");

    }


    @Override
    public void MsgQ_SendMessage(String msgqName, String message, int type) throws EMOMerror, RemoteException {
        MessageQueue queue = findQueue(msgqName);
        if (queue == null) {
            throw new EMOMerror("Queue not found");
        }
        if (!PointToPointQueues.contains(queue)) {
            throw new EMOMerror("Queue is closed");
        }
        Message msg = new Message(type,message);
        queue.addMessage(msg);
        System.out.println("DEBUG -> Message with string " + message +" added to queue " + msgqName);

    }

    @Override
    public String MsgQ_ReceiveMessage(String msgqName, int type) throws EMOMerror, RemoteException {
        MessageQueue queue = findQueue(msgqName);
        if (queue == null) {
            System.out.println("DEBUG -> Queue " + msgqName +" not found");
        }
        if (queue.isEmpty()) {
            System.out.println("DEBUG -> Queue " + msgqName +" is empty, null message will be delivered");
            return null;
        } else {
            Message message = queue.getMessageFromType(type);
            queue.removeMessage(message);
            System.out.println("DEBUG -> Returning message with string " + message.getMessage() +" from queue " + msgqName);
            return message.getMessage();
        }
    }

    @Override
    public void MsgQ_CreateTopic(String topicName, EPublishMode mode) throws EMOMerror , RemoteException{
        boolean found = false;
        for (MessageQueue queue : TopicQueues) {
            if (queue.getName().equals(topicName)) {
                System.out.println("XIVATO4 found value " + found);
                found = true;
                break;
            }
        }
        if (found){
            System.out.println("DEBUG -> Topic Queue " + topicName + " already exists");

        }else {

            //Si no existe la cola, se crea una nueva y se añade al vector que monitoriza todas las colas
            MessageQueue queue = new MessageQueue(topicName, mode);
            System.out.println("DEBUG -> Topic " + topicName + " created");
            TopicQueues.add(queue);
        }
    }

    @Override
    public void MsgQ_CloseTopic(String topicName) throws EMOMerror , RemoteException {
        Iterator<MessageQueue> iterator = TopicQueues.iterator();
        while (iterator.hasNext()) {
            MessageQueue queue = iterator.next();
            if (queue.getName().equals(topicName)) {
                if (queue.isEmpty()) {
                    iterator.remove(); // Safely remove the element from the collection
                    System.out.println("DEBUG -> Topic " + topicName + " closed");
                } else {
                    System.out.println("Cannot close queue " + topicName + " because it still has pending messages.");
                }
                return; // Exit the loop after removing the element
            }
        }
        System.out.println("Queue named " + topicName + " not found");
    }


    @Override
    public void MsgQ_Publish(String topic, String message, int type) throws EMOMerror, RemoteException {
        for (MessageQueue queue : TopicQueues) {
            if (queue.getName().equals(topic)) { //if broadcast{execute func1}else{execute func2}
                if(queue.mode == EPublishMode.BROADCAST){
                    System.out.println("DEBUG -> Publishing message: " + message +" .In topic queue: " + topic + " .In mode:"+queue.mode);
                    publishBroadcast(queue,message);
                }
                else if(queue.mode == EPublishMode.ROUNDROBIN){
                    System.out.println("DEBUG -> Publishing message: " + message +" .In topic queue: " + topic + " .In mode:"+queue.mode);
                    publishRoundRobin(queue,message);
                }
            }else{
                throw new EMOMerror("Queue named " + topic + " not found");
            }
        }

    }

    private void publishBroadcast(MessageQueue queue, String message) throws EMOMerror, RemoteException {
        for (ClientRMI client: queue.getClients()){
            client.onTopicMessage(message);
        }
    }

    private void publishRoundRobin(MessageQueue queue, String message) throws EMOMerror, RemoteException {
        if (!queue.getClients().isEmpty()) { // Check if vector is not empty
            queue.getClients().get(0).onTopicMessage(message); // Use the first element
            queue.getClients().remove(0); // Remove the first element from the copy
        } else {
            // Handle case when vector is empty
            // Example code:
            System.out.println("clients list of queue  " +queue.getName()+" is empty");
        }
    }

    @Override
    public void MsgQ_Subscribe(String topic, ClientRMI listener) throws EMOMerror , RemoteException{
        boolean found = false;
        for (MessageQueue queue : TopicQueues) {
            if (queue.getName().equals(topic)) {
                queue.addClientToTopic(listener);
                System.out.println("DEBUG -> client subscribed to topic " + topic + " this queue now has " + queue.getClients().size() + " elements");
                found = true;
            }
        }
        if (!found){ throw new EMOMerror("Queue named " + topic + " not found");}

    }

    @Override
    public void printHello() throws EMOMerror, RemoteException {
        System.out.println("hello");
    }


    public MessageQueue findQueue(String msgqName) throws EMOMerror , RemoteException{
        boolean queueFound = false;
        //System.out.println(msgqName);
        for (MessageQueue queue : PointToPointQueues) {
            //System.out.println("it is: "+msgqName+",suposed to be " + queue.getName() );

            if (queue.getName().equals(msgqName)) {
                return queue;
            }
        }
        return null; // queue not found
    }


}
