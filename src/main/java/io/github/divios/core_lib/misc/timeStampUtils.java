package io.github.divios.core_lib.misc;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;

public class timeStampUtils {

    /**
     * Serializes a timeStamp on base64
     * @param timestamp The timestamp to serialize
     * @return String representing the timeStamp on base64
     */
    public static String serialize(Timestamp timestamp) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream dataOutput = new ObjectOutputStream(outputStream);

            dataOutput.writeObject(timestamp);

            return Base64Coder.encodeLines(outputStream.toByteArray());

        } catch (IOException e) {
            throw new IllegalStateException("Unable to serialize timeStamp.", e);
        }
    }

    /**
     * Deserializes a timeStamp from a base64 String
     * @param base64 The base64 serialized timeStamp
     * @return The deserialized TimeStamp
     */
    public static Timestamp deserialize(String base64) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            ObjectInputStream dataInput = new ObjectInputStream(inputStream);

            Timestamp _return = (Timestamp) dataInput.readObject();
            dataInput.close();

            return _return;

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to serialize timeStamp.", e);
        }
    }

    /**
     * Returns the time in minutes
     * @param date1
     * @param date2
     * @return
     */
    public static double diff(Timestamp date1, Timestamp date2) {
        long mSeconds = date2.getTime() - date1.getTime();
        double seconds = mSeconds / 1000;

        return seconds;
    }

}
