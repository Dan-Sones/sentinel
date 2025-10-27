package com.sentinel.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class Conversion {

  private String eventUuid;
  private String timestampEventTime;
  private String ipAddress;
  private String adUuid;
  private String conversionType;

  @JsonCreator
  public Conversion(
      @JsonProperty("event_uuid") String eventUUid,
      @JsonProperty("timestamp_event_time") String timestampEventTime,
      @JsonProperty("ip_address") String ipAddress,
      @JsonProperty("ad_uuid") String adUuid,
      @JsonProperty("conversion_type") String conversionType) {
    this.eventUuid = eventUUid;
    this.timestampEventTime = timestampEventTime;
    this.ipAddress = ipAddress;
    this.adUuid = adUuid;
    this.conversionType = conversionType;
  }
}
