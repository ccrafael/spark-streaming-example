package producer;

import model.Pojo;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * Produce Pojos to a socket.
 */
public class Producer implements Closeable {

    private final String ip;
    private final int port;
    private final double SEC = 1_000_000_000D;

    private ServerSocket producer;
    private Serializer serializer;

    /**
     * Bind'em all.
     *
     * @param ip   The bind ip address.
     * @param port The port to bind.
     * @throws IOException
     */
    public Producer(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;


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
     * @param out The stream.
     * @throws IOException
     */
    private void sendPojos(BufferedOutputStream out) throws IOException {
        long time = System.nanoTime();
        long delta;
        long sent = 0;
        serializer.init(out);
        Pojo pojo = createPojo();

        while (true) {

            serializer.serialize(pojo);

            sent++;

            delta = (System.nanoTime() - time);
            if (delta > 5 * SEC) {
                System.out.println(String.format(" Sending %f Pojos/s", sent / (delta / SEC)));
                time = System.nanoTime();
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

        pojo.s1 = String.valueOf("foo");
        pojo.s2 = String.valueOf("bar");
        pojo.s3 = String.valueOf("foobar");

        Integer ints[] = new Integer[10];
        pojo.ints = IntStream.range(0, 10).boxed().collect(Collectors.toList()).toArray(ints);


        Double doub[] = new Double[10];
        pojo.doubles = DoubleStream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).boxed().collect(Collectors.toList()).toArray(doub);


        pojo.map = IntStream.range(0, 10).boxed().collect(Collectors.toMap(String::valueOf, Function.identity()));

        pojo.map.put("check", 1); // use this value to check that all is right


        pojo.i1 = 1;
        pojo.i2 = 2;
        pojo.i3 = 3;
        pojo.i4 = 4;
        pojo.i5 = 5;
        pojo.i6 = 6;
        pojo.i7 = 7;
        pojo.i8 = 8;

        pojo.l1 = 1;
        pojo.l2 = 2;
        pojo.l3 = 3;
        pojo.l4 = 4;
        pojo.l5 = 5;
        pojo.l6 = 6;
        pojo.l7 = 7;
        pojo.l8 = 8;

        pojo.d1 = 1;
        pojo.d2 = 2;
        pojo.d3 = 3;
        pojo.d4 = 4;
        pojo.d5 = 5;
        pojo.d6 = 6;
        pojo.d7 = 7;
        pojo.d8 = 8;


        return pojo;
    }

    public static void main(String[] args) {

        try (Producer p = new Producer("localhost", 8080)) {
            p.setSerializer(SerializerFactory.PROTOSTUFF);
            p.produce();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }


}
