package Stream;

import Services.Handler;
import network.ConnectionFactory;
import network.IConnectionNames;
import network.MulticastSender;
import org.xerial.snappy.Snappy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Queue;

public class Sender implements Runnable {

    @Override
    public void run() {
        try{
            MulticastSender multicastSender= (MulticastSender) ConnectionFactory.getIConnection(IConnectionNames.MULTICAST_SENDER);
            multicastSender.initialize(50001,"239.0.0.1");//multicast address from the config file
            //MulticastSocket multicastSocket = new MulticastSocket();
            //InetAddress multicastGroup = InetAddress.getByName("239.0.0.1"); // Multicast IP address
            System.out.println("im heere");
            while(true){
                if(Handler.baos.isEmpty())
                    continue;
                byte[] compressed= Handler.baos.remove();
                int chunkSize = 65507;
                int totalChunks = (int) Math.ceil((double) compressed.length / chunkSize);

                for (int chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
                    int offset = chunkIndex * chunkSize;
                    int length = Math.min(chunkSize, compressed.length - offset);
                    byte[] chunkBuffer = new byte[length];
                    System.arraycopy(compressed, offset, chunkBuffer, 0, length);

                    //DatagramPacket datagramPacket = new DatagramPacket(chunkBuffer, chunkBuffer.length, multicastGroup, 1234);
                    multicastSender.send(chunkBuffer);
                }
            }

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}