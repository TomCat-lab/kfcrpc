package com.cola.kfcrpc.core.test;

import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

import java.io.IOException;

public class TestZkServer {
  TestingServer testingServer =null;

  public void start() {

      try {
        testingServer = new TestingServer(2182);
          testingServer.start();
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
  }

  public void stop()  {
      try {
          testingServer.stop();
          CloseableUtils.closeQuietly(testingServer);
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
  }
}
