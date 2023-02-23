package com.jd.bluedragon.distribution.jdq4;

import com.jd.bdp.jdw.avro.JdwData;
import com.jd.bdp.jdw.avro.JdwDataSchema;
import java.io.IOException;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.serialization.Deserializer;

public class AvroDataDeserializer implements Deserializer<JdwData> {
    private DatumReader<JdwData> reader = new AvroDataDeserializer.AvroReader(JdwDataSchema.createSCHEMA());
    private BinaryDecoder binaryDecoder;
    private BinaryDecoderV2 binaryDecoderV2;

    public AvroDataDeserializer() {
    }

    public JdwData deserialize(String s, byte[] bytes) {
        this.binaryDecoder = DecoderFactory.get().binaryDecoder(bytes, this.binaryDecoder);

        try {
            return (JdwData)this.reader.read(null, this.binaryDecoder);
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    public JdwData deserializeV2(String s, byte[] bytes) {
        if (this.binaryDecoderV2 == null) {
            this.binaryDecoderV2 = new BinaryDecoderV2(bytes, 0, bytes.length);
        } else {
            this.binaryDecoderV2.configure(bytes, 0, bytes.length);
        }

        try {
            return (JdwData)this.reader.read(null, this.binaryDecoderV2);
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    public void configure(Map<String, ?> map, boolean b) {
    }

    public void close() {
    }

    class AvroReader<T> extends SpecificDatumReader<T> {
        public AvroReader(Schema schema) {
            super(schema, schema, new SpecificData(JdwData.class.getClassLoader()));
        }

        protected Object readString(Object old, Schema expected, Decoder in) throws IOException {
            return in.readString();
        }
    }
}
