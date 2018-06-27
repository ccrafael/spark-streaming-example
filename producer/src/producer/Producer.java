package producer;

import model.Pojo;
import sun.misc.Perf;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Produce Pojos to a socket.
 */
public class Producer implements Closeable {

    private final Random rand;
    private final String ip;
    private final int port;
    private final Perf perf;
    private ServerSocket producer;

    /**
     * Bind'em all.
     *
     * @param ip The bind ip address.
     * @param port The port to bind.
     * @throws IOException
     */
    public Producer(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        this.rand = new Random();
        this.perf = Perf.getPerf();

        this.producer = new ServerSocket();
        this.producer.bind(new InetSocketAddress("localhost", 8080));
    }


    /**
     * Produce Pojos as if there were no tomorrow.
     */
    public void produce() {
        BufferedOutputStream out;

        while (true) {
            System.out.println("Waiting for a client ...");

            try (Socket client = producer.accept()) {

                out = new BufferedOutputStream(client.getOutputStream());

                sendPojos(out);

            } catch (IOException e) {
                System.out.println("D'oh!!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Show me the Pojos. As fast as I can or as fast as the consumer can read and until
     * the end of the days.
     *
     * @param out Sink the pojos to this stream.
     * @throws IOException
     */
    private void sendPojos(OutputStream out) throws IOException {
        long time = perf.highResCounter();
        long countFreq = perf.highResFrequency();
        long sent = 0;

        ObjectOutputStream serializer = new ObjectOutputStream(out);

        while (true) {

            Pojo pojo = createPojo();
            serializer.writeObject(pojo);
            serializer.flush();

            // This is needed to improve performance
            serializer.reset();

            sent++;

            if ((perf.highResCounter() - time) * 1L / countFreq > 1) {
                System.out.println(String.format(" Sending %d Pojos/s", sent));
                time = perf.highResCounter();
                sent = 0;
            }
        }
    }


    @Override
    public void close() throws IOException {
        producer.close();
    }


    private Pojo createPojo() {
        Pojo pojo = new Pojo();

        pojo.s1 = String.valueOf(rand.nextLong());
        pojo.s2 = String.valueOf(rand.nextLong());
        pojo.s3 = String.valueOf(rand.nextLong());

        pojo.i1 = rand.nextInt();
        pojo.i2 = rand.nextInt();
        pojo.i3 = rand.nextInt();
        pojo.i4 = rand.nextInt();
        pojo.i5 = rand.nextInt();
        pojo.i6 = rand.nextInt();
        pojo.i7 = rand.nextInt();
        pojo.i8 = rand.nextInt();

        pojo.l1 = rand.nextLong();
        pojo.l2 = rand.nextLong();
        pojo.l3 = rand.nextLong();
        pojo.l4 = rand.nextLong();
        pojo.l5 = rand.nextLong();
        pojo.l6 = rand.nextLong();
        pojo.l7 = rand.nextLong();
        pojo.l8 = rand.nextLong();

        pojo.d1 = rand.nextDouble();
        pojo.d2 = rand.nextDouble();
        pojo.d3 = rand.nextDouble();
        pojo.d4 = rand.nextDouble();
        pojo.d5 = rand.nextDouble();
        pojo.d6 = rand.nextDouble();
        pojo.d7 = rand.nextDouble();
        pojo.d8 = rand.nextDouble();

        return pojo;
    }

    public static void main(String[] args)  {
        try (Producer p = new Producer("localhost", 8080)) {
            p.produce();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
