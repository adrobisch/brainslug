package brainslug.jpa.util;

import java.io.*;

public class ObjectSerializer {
  public byte[] serialize(Object value) {
    if (!(value instanceof Serializable)) {
      throw new IllegalArgumentException("object must be serializable: " + value);
    }

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
      objectOutputStream.writeObject(value);
      objectOutputStream.flush();
      objectOutputStream.close();
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Object deserialize(byte[] bytes) {
    try {
      ObjectInputStream s = new ObjectInputStream(new ByteArrayInputStream(bytes));
      return s.readObject();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
