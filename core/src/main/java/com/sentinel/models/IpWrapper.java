package com.sentinel.models;

import lombok.Data;

@Data
public class IpWrapper {

  private String ipAddressKey;

  private Conversion conversion;
  private Click click;

  public static IpWrapper fromConversion(Conversion conversion) {
    IpWrapper ipWrapper = new IpWrapper();
    ipWrapper.ipAddressKey = conversion.getIpAddress();
    ipWrapper.conversion = conversion;
    return ipWrapper;
  }

  public static IpWrapper fromClick(Click click) {
    IpWrapper ipWrapper = new IpWrapper();
    ipWrapper.ipAddressKey = click.getIpAddress();
    ipWrapper.click = click;
    return ipWrapper;
  }
}
