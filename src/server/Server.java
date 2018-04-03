package server;

import server.ignition.ServiceIgniter;

/**
 * @author Euijin.Ham
 * @version 1.0.0
 * A EngineTest Entrance Class for igniting both of communication server and service server
 * Jul-21-2017
 */
public class Server {

    public static void main(String... args){
        ServiceIgniter.getInstance().igniteServiceServer();
    }

}
