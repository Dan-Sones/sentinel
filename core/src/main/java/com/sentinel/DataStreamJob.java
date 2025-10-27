package com.sentinel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sentinel.models.Click;
import com.sentinel.models.Conversion;
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

  private static void configureSsl(
      KafkaSourceBuilder<String> builder, Map<String, String> sslProperties) {
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
  }

  public static KafkaSource<String> getClicksSource(
      String bootstrapServers, Map<String, String> sslProperties) {

    KafkaSourceBuilder<String> builder =
        KafkaSource.<String>builder()
            .setBootstrapServers(bootstrapServers)
            .setTopics("clicks")
            .setGroupId("sentinel-flink-job-clicks")
            .setProperty("client.id", "sentinel-flink-clicks")
            .setStartingOffsets(OffsetsInitializer.latest())
            .setValueOnlyDeserializer(new SimpleStringSchema());

    configureSsl(builder, sslProperties);

    return builder.build();
  }

  public static KafkaSource<String> getConversionsSource(
      String bootstrapServers, Map<String, String> sslProperties) {

    KafkaSourceBuilder<String> builder =
        KafkaSource.<String>builder()
            .setBootstrapServers(bootstrapServers)
            .setTopics("conversions")
            .setGroupId("sentinel-flink-job-conversions")
            .setProperty("client.id", "sentinel-flink-conversions")
            .setStartingOffsets(OffsetsInitializer.latest())
            .setValueOnlyDeserializer(new SimpleStringSchema());

    configureSsl(builder, sslProperties);

    return builder.build();
  }

  public static DataStream<Click> getClicksStream(
      StreamExecutionEnvironment env, Map<String, String> sslProperties, String bootstrapServers) {

    KafkaSource<String> clicksSource = getClicksSource(bootstrapServers, sslProperties);

    DataStream<String> clicksJsonStream =
        env.fromSource(clicksSource, WatermarkStrategy.noWatermarks(), "Kafka Source (Clicks)");

    return clicksJsonStream
        .map(clickJson -> getObjectMapper().readValue(clickJson, Click.class))
        .name("Parse Payment json into class");
  }

  public static DataStream<Conversion> getConversionsStream(
      StreamExecutionEnvironment env, Map<String, String> sslProperties, String bootstrapServers) {
    KafkaSource<String> conversionsSource = getConversionsSource(bootstrapServers, sslProperties);

    DataStream<String> conversionsJsonStream =
        env.fromSource(
            conversionsSource, WatermarkStrategy.noWatermarks(), "Kafka Source (Conversions)");

    return conversionsJsonStream
        .map(conversionJson -> getObjectMapper().readValue(conversionJson, Conversion.class))
        .name("Parse conversion json into class");
  }

  public static void main(String[] args) throws Exception {
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    String bootstrapServers = "kafka:9092";
    Map<String, String> sslProperties = new HashMap<>();

    DataStream<Click> clicksDataStream = getClicksStream(env, sslProperties, bootstrapServers);


    DataStream<Conversion> conversionDataStream =
        getConversionsStream(env, sslProperties, bootstrapServers);


    env.execute("Flink Java API Skeleton");
  }
}
