package com.esprit.android.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by khmai on 08/03/2018.
 */

public class NetworkUtil extends android.os.AsyncTask {



    @Override
    protected Object doInBackground(Object[] objects) {
        //do something asynchronously

        return isInternetAvailable("8.8.8.8", 53, 1000);
    }


    public boolean isInternetAvailable(String address, int port, int timeoutMs) {
        try {
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress(address, port);

            sock.connect(sockaddr, timeoutMs); // This will block no more than timeoutMs
            sock.close();

            return true;

        } catch (IOException e) { return false; }
    }




}
