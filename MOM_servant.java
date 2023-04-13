
import java.rmi.RemoteException;
import java.util.Vector;

public class MOM_servant implements MOM_interface{
    private Vector<MessageQueue> PointToPointQueues = new Vector<MessageQueue>();//una cola punto a punto y una cola topic si pueden tener el mismo nombre
    private Vector<MessageQueue> TopicQueues = new Vector<MessageQueue>();
    @Override
    public void MsgQ_CreateQueue(String msgqName) throws EMOMerror, RemoteException {
        //Se comprueba si ya existe una cola con el nombre introducido
        System.out.println("XIVATO2");
        for (MessageQueue queue : PointToPointQueues) {
            if (queue.getName().equals(msgqName)) {
                throw new EMOMerror("Queue with name " + msgqName + " already exists : " + queue.getName());
            }
        }
        //Si no existe la cola, se crea una nueva y se añade al vector que monitoriza todas las colas
        MessageQueue queue = new MessageQueue(msgqName);

        PointToPointQueues.add(queue);
        System.out.println("Queue :" + PointToPointQueues.get(0).getName() +" added");
    }

    @Override
    public void MsgQ_CloseQueue(String msgqName) throws EMOMerror, RemoteException {
        MessageQueue queue = findQueue(msgqName);

        if (queue.isEmpty()) {
            PointToPointQueues.remove(queue);
        } else {
            throw new EMOMerror("Cannot close queue " + msgqName + " because it still has pending messages.");
        }

    }


    @Override
    public void MsgQ_SendMessage(String msgqName, String message, int type) throws EMOMerror, RemoteException {
        MessageQueue queue = findQueue(msgqName);
        if (queue == null) {
            throw new EMOMerror("Queue not found");
        }
        if (!PointToPointQueues.contains(queue)) {//s'ha de mirar com va aquest contains
            throw new EMOMerror("Queue is closed");
        }
        Message msg = new Message(type,message);
        queue.addMessage(msg);
    }

    @Override
    public String MsgQ_ReceiveMessage(String msgqName, int type) throws EMOMerror, RemoteException {
        MessageQueue queue = findQueue(msgqName);
        if (queue == null) {
            throw new EMOMerror("Queue not found");
        }
        if (PointToPointQueues.contains(queue)) {
            if (queue.isEmpty()) {
                throw new EMOMerror("Queue is closed and empty");
            } else {
                throw new EMOMerror("Queue is closed");
            }
        }
        Message message = queue.getMessageFromType(type);
        queue.removeMessage(message);
        if(message != null){
            return message.getMessage();
        }
        return null;
    }

    @Override
    public void MsgQ_CreateTopic(String topicName, EPublishMode mode) throws EMOMerror , RemoteException{
        System.out.println("XIVATO3");
        for (MessageQueue queue : TopicQueues) {
            if (queue.getName().equals(topicName)) {
                throw new EMOMerror("Queue with TOPIC name " + topicName + " already exists :" + queue.getName());
            }
        }
        //Si no existe la cola, se crea una nueva y se añade al vector que monitoriza todas las colas
        MessageQueue queue = new MessageQueue(topicName,mode);
        TopicQueues.add(queue);
    }

    @Override
    public void MsgQ_CloseTopic(String topicName) throws EMOMerror , RemoteException{
        for (MessageQueue queue : TopicQueues) {
            if (queue.getName().equals(topicName)) {
                if (queue.isEmpty()) {
                    TopicQueues.remove(queue);
                } else {
                    throw new EMOMerror("Cannot close queue " + topicName + " because it still has pending messages.");
                }
            }else{
                throw new EMOMerror("Queue named " + topicName + " not found");
            }
        }
    }

    @Override
    public void MsgQ_Publish(String topic, String message, int type) throws EMOMerror, RemoteException {
        for (MessageQueue queue : TopicQueues) {
            if (queue.getName().equals(topic)) { //if broadcast{execute func1}else{execute func2}
                Message message1 = new Message(type,message);
                queue.addMessage(message1);
            }else{
                throw new EMOMerror("Queue named " + topic + " not found");
            }
        }
        System.out.println("Messagewith string " + message +" added");

    }

    @Override
    public void MsgQ_Subscribe(String topic, ClientRMI listener) throws EMOMerror , RemoteException{
        for (MessageQueue queue : TopicQueues) {
            if (queue.getName().equals(topic)) {
                queue.addClientToTopic(listener);
            }else{
                throw new EMOMerror("Queue named " + topic + " not found");
            }
        }
    }

    @Override
    public void printHello() throws EMOMerror, RemoteException {
        System.out.println("hello");
    }


    public MessageQueue findQueue(String msgqName) throws EMOMerror , RemoteException{
        boolean queueFound = false;
        System.out.println(msgqName);
        for (MessageQueue queue : PointToPointQueues) {
            System.out.println("it is : "+msgqName+"suposed to be " + queue.getName() );

            if (queue.getName().equals(msgqName)) {
                return queue;
            }
        }
        if (!queueFound) {
            throw new EMOMerror("Queue " + msgqName + " not found.");
        }
        return null; // queue not found
    }


}
