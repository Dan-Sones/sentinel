
package com.sentinel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sentinel.models.Click;
import java.util.HashMap;
import java.util.Map;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class DataStreamJob {

  public static ObjectMapper getObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }

  public static KafkaSource<String> getClicksSource(
      String bootstrapServers, Map<String, String> sslProperties) {

    KafkaSourceBuilder<String> builder =
        KafkaSource.<String>builder()
            .setBootstrapServers(bootstrapServers)
            .setTopics("clicks")
            .setGroupId("sentinel-flink-job")
            .setStartingOffsets(OffsetsInitializer.latest())
            .setValueOnlyDeserializer(new SimpleStringSchema());

    if (sslProperties.get("ssl.keystore.key") != null) {
      builder.setProperty("security.protocol", "SSL");
      builder.setProperty("ssl.keystore.type", "PEM");
      builder.setProperty("ssl.truststore.type", "PEM");
      builder.setProperty("ssl.keystore.key", sslProperties.get("ssl.keystore.key"));
      builder.setProperty(
          "ssl.truststore.certificates", sslProperties.get("ssl.truststore.certificates"));
      builder.setProperty(
          "ssl.keystore.certificate.chain", sslProperties.get("ssl.keystore.certificate.chain"));
    }

    return builder.build();
  }

  public static void main(String[] args) throws Exception {
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    String bootstrapServers = "kafka:9092";
    Map<String, String> sslProperties = new HashMap<>();

    KafkaSource<String> clicksSource = getClicksSource(bootstrapServers, sslProperties);

    DataStream<String> clicksJsonStream =
        env.fromSource(clicksSource, WatermarkStrategy.noWatermarks(), "Kafka Source (Clicks)");

    DataStream<Click> clicksDataStream =
        clicksJsonStream
            .map(clickJson -> getObjectMapper().readValue(clickJson, Click.class))
            .name("Parse Payment json into class");

    clicksDataStream.map(Click::toString).print();

    env.execute("Flink Java API Skeleton");
  }
}
