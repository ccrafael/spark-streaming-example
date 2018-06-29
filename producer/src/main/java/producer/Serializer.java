package producer;

import model.Pojo;

import java.io.IOException;
import java.io.OutputStream;

public interface Serializer {

    void init(OutputStream outputstream) throws IOException;
    void serialize(Pojo pojo) throws IOException;
}
