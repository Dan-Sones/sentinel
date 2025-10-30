package com.sentinel;

import com.sentinel.models.SessionAggregationResult;
import com.sentinel.models.SessionWindowResult;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class SessionProcessWindowFunction
    extends ProcessWindowFunction<
        SessionAggregationResult, SessionWindowResult, String, TimeWindow> {

  @Override
  public void process(
      String s,
      ProcessWindowFunction<SessionAggregationResult, SessionWindowResult, String, TimeWindow>
              .Context
          context,
      Iterable<SessionAggregationResult> iterable,
      Collector<SessionWindowResult> collector)
      throws Exception {
    long windowStart = context.window().getStart();
    long windowEnd = context.window().getEnd();
    long durationMillis = windowEnd - windowStart;
    long durationSeconds = durationMillis / 1000;

    SessionAggregationResult sessionAggregationResult = iterable.iterator().next();

    long clickVelocity = sessionAggregationResult.getClicks() / durationSeconds;

    long conversionRate;
    if (sessionAggregationResult.getConversions() == 0
        || sessionAggregationResult.getClicks() == 0) {
      conversionRate = 0;
    } else {
      conversionRate =
          sessionAggregationResult.getConversions() / sessionAggregationResult.getClicks();
    }

    collector.collect(new SessionWindowResult(conversionRate, clickVelocity));
  }
}
