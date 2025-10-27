package com.sentinel.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class Click {

  //    {
  //        "event_uuid": "",
  //        "timestamp_event_time": "",
  //        "ip_address": "",
  //        "ad_uuid": "",
  //    }

  private String eventUuid;
  private String timestampEventTime;
  private String ipAddress;
  private String adId;

  @JsonCreator
  public Click(
      @JsonProperty("event_uuid") String eventUuid,
      @JsonProperty("timestamp_event_time") String timestampEventTime,
      @JsonProperty("ip_address") String ipAddress,
      @JsonProperty("ad_uuid") String adId) {

    this.eventUuid = eventUuid;
    this.timestampEventTime = timestampEventTime;
    this.ipAddress = ipAddress;
    this.adId = adId;
  }
}
