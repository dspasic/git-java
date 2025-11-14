package git;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class ZlibCompressor {

  private static final int BUFFER_SIZE = 8192;

  public static byte[] compress(byte[] data) throws IOException {
    var baos = new ByteArrayOutputStream();
    try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(baos)) {
      deflaterOutputStream.write(data);
    }
    return baos.toByteArray();
  }

  public static byte[] decompress(byte[] data) throws IOException {
    var baos = new ByteArrayOutputStream();
    try (InflaterInputStream inflaterInputStream =
        new InflaterInputStream(new ByteArrayInputStream(data))) {
      byte[] buffer = new byte[BUFFER_SIZE];
      int len;
      while ((len = inflaterInputStream.read(buffer)) != -1) {
        baos.write(buffer, 0, len);
      }
    }
    return baos.toByteArray();
  }
}
