
//Crear interfaz
//Clase implementa interfaz (servant)
//Servidor extendra el servant
//Servidor dara de alta un objeto de tipo servant en el registrandlo en el rmiRegistry
//Cliente hara lookup obteniendo referencia a objeto remoto, con este objeto remoto el cliente podra invocar los metodos que tenga que seran los remotos

//(Callback) en el servidor se añadira un metodo (RegisterClient(Client client1)) en la interfaz que como parametro se le pasara un objeto servantClient
//Tambien añadira metodo para dar de baja cliente


//Parte cliente (Con callbacks)
//Crear interfaz metodos clientes
//crear clase servantClient que implementa metodos de la interfaz cliente
//Cliente usara el metodo remoto (RegisterClient) del objeto del servidor para pasarle el objeto (clase ClientRemoteObject con metodos callbacks)


import java.rmi.RemoteException;

//Al usar metodos remotos del objeto remoto habra que controlar errores(Mirar ejemplo Bombilla)
public interface MOM_interface extends java.rmi.Remote {
    void MsgQ_CreateQueue(String msgqName) throws EMOMerror, RemoteException;
    void MsgQ_CloseQueue(String msgqName) throws EMOMerror, RemoteException;
    void MsgQ_SendMessage(String msgqName, String message, int type) throws EMOMerror, RemoteException;
    String MsgQ_ReceiveMessage(String msgqName, int type) throws EMOMerror, RemoteException;
    void MsgQ_CreateTopic(String topicName, EPublishMode mode) throws EMOMerror, RemoteException;
    void MsgQ_CloseTopic(String topicName) throws EMOMerror, RemoteException;
    void MsgQ_Publish(String topic, String message, int type) throws EMOMerror, RemoteException;
    void MsgQ_Subscribe(String topic, ClientRMI listener) throws EMOMerror, RemoteException;

    void printHello() throws EMOMerror, RemoteException;
}
