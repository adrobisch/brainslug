package brainslug.jpa.util;

import java.io.*;

public class ObjectSerializer {
  public String serialize(Object value) {
    if (!(value instanceof Serializable)) {
      throw new IllegalArgumentException("object must be serializable: " + value);
    }

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
      objectOutputStream.writeObject(value);
      objectOutputStream.flush();
      objectOutputStream.close();
      return new String(outputStream.toString("ISO-8859-1"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Object deserialize(String stringValue) {
    try {
      ObjectInputStream s = new ObjectInputStream(new ByteArrayInputStream(stringValue.getBytes("ISO-8859-1")));
      return s.readObject();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException();
    }
  }
}
