package org.planit.utils;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.planit.utils.reflection.StackWalkerUtil;

/**
 * Test the Reflection utils
 * 
 * @author markr
 *
 */
public class ReflectionTest {

  @Before
  public void intialise() {
  }
  
  /**
   * Test some of the stackwalker utils
   */
  @Test
  public void theStackWalkerTest() { 
    assertEquals("theStackWalkerTest", StackWalkerUtil.getCallingMethodName());
    assertEquals("org.planit.utils.ReflectionTest", StackWalkerUtil.getCallingClassName());
    assertEquals(0, StackWalkerUtil.getCallingMethodType().parameterCount());
  }


}
