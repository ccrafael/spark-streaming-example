package producer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import model.Message;
import model.Pojo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class SerializerFactory {


    public static final Serializer JAVA = new Serializer() {
        ObjectOutputStream envelopSerializer;
        ObjectOutputStream serializer;
        ByteArrayOutputStream buffer;

        public void init(OutputStream outputstream) throws IOException {
            envelopSerializer = new ObjectOutputStream(outputstream);
            serializer = new ObjectOutputStream(buffer);
        }

        public void serialize(Pojo pojo) throws IOException {
            serializer.writeObject(pojo);
            serializer.flush();

            Message message = new Message(buffer.toByteArray());
            envelopSerializer.writeObject(message);
            envelopSerializer.flush();

            // reset
            envelopSerializer.reset();
            serializer.reset();
            buffer.reset();
        }
    };

    public static final Serializer PROTOSTUFF = new Serializer() {
        Schema<Pojo> schema = RuntimeSchema.getSchema(Pojo.class);
        Schema<Message> envelopSchema = RuntimeSchema.getSchema(Message.class);

        LinkedBuffer buffer = LinkedBuffer.allocate(2048);
        OutputStream outputstream;

        public void init(OutputStream outputstream) throws IOException {
            this.outputstream = outputstream;
        }

        public void serialize(Pojo pojo) throws IOException {
            try {

                Message message = new Message(ProtostuffIOUtil.toByteArray( pojo, schema, buffer));
                buffer.clear();
                ProtostuffIOUtil.writeDelimitedTo(outputstream, message, envelopSchema, buffer);

            }finally {
                buffer.clear();
            }
        }
    };
}
