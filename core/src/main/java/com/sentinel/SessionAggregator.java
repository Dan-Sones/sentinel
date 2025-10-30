package com.sentinel;

import com.sentinel.models.IpWrapper;
import com.sentinel.models.SessionAggregationResult;
import org.apache.flink.api.common.functions.AggregateFunction;

public class SessionAggregator
    implements AggregateFunction<IpWrapper, SessionAggregationResult, SessionAggregationResult> {

  @Override
  public SessionAggregationResult createAccumulator() {
    return new SessionAggregationResult();
  }

  @Override
  public SessionAggregationResult add(
      IpWrapper ipWrapper, SessionAggregationResult sessionAggregationResult) {
    if (ipWrapper.getClick() != null) {
      sessionAggregationResult.IncrementClicks();
    } else if (ipWrapper.getConversion() != null) {
      sessionAggregationResult.IncrementConversions();
    }

    return sessionAggregationResult;
  }

  @Override
  public SessionAggregationResult getResult(SessionAggregationResult sessionAggregationResult) {
    return sessionAggregationResult;
  }

  @Override
  public SessionAggregationResult merge(
      SessionAggregationResult acc1, SessionAggregationResult acc2) {
    acc1.setClicks(acc1.getClicks() + acc2.getClicks());
    acc1.setConversions(acc1.getConversions() + acc2.getConversions());
    return acc1;
  }
}
