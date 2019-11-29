package org.dew.swingup.demo;

import org.dew.swingup.IApplicationListener;
import org.dew.swingup.fm.FMService;
import org.dew.swingup.rpc.GuiLocalRPCClient;

public class DemoApplicationListener implements IApplicationListener {

  @Override
  public void start() {
    System.out.println("DemoApplicationListener.start");
    
    GuiLocalRPCClient.addService(new FMService(), "FM", "FM Service");
  }

  @Override
  public void afterLogin() {
    System.out.println("DemoApplicationListener.afterLogin");
  }

  @Override
  public void beforeLogout() {
    System.out.println("DemoApplicationListener.beforeLogout");
  }

  @Override
  public void end() {
    System.out.println("DemoApplicationListener.end");
  }

}
