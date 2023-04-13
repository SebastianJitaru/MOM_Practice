
import java.util.Vector;

public class MessageQueue{
        private String name;
        private Vector<Message> messages;

        private Vector<ClientRMI> clients;
        EPublishMode mode;
        public MessageQueue(String name) {
            this.name = name;
            this.messages = new Vector<Message>();
        }
        public MessageQueue(String name, EPublishMode mode){
            this.name = name;
            this.messages = new Vector<Message>();
            this.mode = mode;
            this.clients = new Vector<ClientRMI>();
        }

        public String getName() {
            return name;
        }

        public void addMessage(Message message) {
            messages.add(message);
        }

        public void addClientToTopic(ClientRMI client){ //falte mirar si client ja esta al topic ...
            clients.add(client);
        }
        public void removeMessage(Message message){messages.remove(message);}

        public Vector<Message> getMessages() {
            return messages;
        }

        public Message getMessageFromType(int type){
            for(Message message : messages){
                if(message.getType() == type){
                    return message;
                }
            }
            return null;
        }

    public boolean isEmpty() {
        synchronized(messages) {
            return messages.isEmpty();
        }
    }

}
