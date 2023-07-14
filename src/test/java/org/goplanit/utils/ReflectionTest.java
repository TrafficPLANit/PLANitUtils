package org.goplanit.utils;


import org.goplanit.utils.reflection.StackWalkerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the Reflection utils
 * 
 * @author markr
 *
 */
public class ReflectionTest {

  @BeforeEach
  public void intialise() {
  }
  
  /**
   * Test some of the stackwalker utils
   */
  @Test
  public void theStackWalkerTest() { 
    assertEquals("theStackWalkerTest", StackWalkerUtil.getCallingMethodName());
    assertEquals("org.goplanit.utils.ReflectionTest", StackWalkerUtil.getCallingClassName());
    assertEquals(0, StackWalkerUtil.getCallingMethodType().parameterCount());
  }


}
