package com.sentinel.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SessionAggregationResult {
  private Integer clicks;
  private Integer conversions;

  public void IncrementClicks() {
    this.clicks += 1;
  }

  public void IncrementConversions() {
    this.conversions += 1;
  }
}
