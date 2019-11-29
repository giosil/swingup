package org.dew.test;

import org.dew.swingup.ResourcesMgr;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestSwingup extends TestCase {
  
  public TestSwingup(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    return new TestSuite(TestSwingup.class);
  }
  
  public void testApp() {
    System.out.println(ResourcesMgr.sPREFIX + " build " + ResourcesMgr.sBUILD);
  }
  
}
