package com.sentinel.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Click {

  //    {
  //        "event_id": "",
  //        "timestamp_event_time": "",
  //        "ip_address": "",
  //        "ad_uuid": "",
  //    }

  private String eventId;
  private String timestampEventTime;
  private String ipAddress;
  private String adId;

  @JsonCreator
  public Click(
      @JsonProperty("event_id") String eventId,
      @JsonProperty("timestamp_event_time") String timestampEventTime,
      @JsonProperty("ip_address") String ipAddress,
      @JsonProperty("ad_uuid") String adId) {

    this.eventId = eventId;
    this.timestampEventTime = timestampEventTime;
    this.ipAddress = ipAddress;
    this.adId = adId;
  }
}
