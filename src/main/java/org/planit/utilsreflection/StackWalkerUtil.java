package org.planit.utilsreflection;

import java.lang.StackWalker.StackFrame;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Utils class warpping the Stackwalker API functionality in easy to use method calls
 * 
 * @author markr
 *
 */
public class StackWalkerUtil {
  
  private static StackWalker getStackWalkerInstance() {
    return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
  }
  
  /** Method to convert the stream to a list of stack frames that can be used for reflection
   * @param stackFrameStream
   * @return list of stackFrames
   */
  private static List<StackWalker.StackFrame> convertStreamToList(Stream<StackWalker.StackFrame> stackFrameStream){
    return stackFrameStream.collect(Collectors.toList());
  }

  /** collect the current stackframes as a list
   * @return stack frame list
   */
  public static List<StackWalker.StackFrame> getStackFramesAsList(){
    return getStackWalkerInstance().walk( StackWalkerUtil::convertStreamToList);
  }
  
  /**
   * Collect the first available stack frame if it exists.
   * @return first stack frame as an optional
   */
  public static Optional<StackWalker.StackFrame> getFirstStackFrame() {
    return getStackWalkerInstance().walk( stackFrameStream -> stackFrameStream.findFirst()); 
  }
  
  /**
   * Skip any number of stack frames and return the stream at this offset point
   * @param skipOffset
   * @return stack frame at offset point, null if not present
   */
  public static StackFrame getStackFrameWithOffset(int skipOffset) {
    return getStackWalkerInstance().walk( stackFrameStream -> stackFrameStream.skip(skipOffset).findFirst().orElse(null));
  }
  
  /**
   * Collect the method name of the method that is the invoker of calling this method
   * @return calling method name, "unknown" if unknown
   */
  public static String getCallingMethodName() {
    // we skip the frames reflecting method calls after the invoker called this method
    StackFrame callingStackFrame = getStackFrameWithOffset(2);
    return callingStackFrame!=null ? callingStackFrame.getMethodName() : "unknown";
  }
  
  /**
   * Collect the class name of the method that is the invoker of calling this method
   * @return calling class name, "unknown" if unknown
   */
  public static String getCallingClassName() {
    // we skip the frames reflecting method calls after the invoker called this method
    StackFrame callingStackFrame = getStackFrameWithOffset(2);
    return callingStackFrame!=null ? callingStackFrame.getClassName() : "unknown";
  }
  
  /**
   * Collect the method type of the method that is the invoker of calling this method
   * @return calling method type, null if unknown
   */
  public static MethodType getCallingMethodType() {
    // we skip the frames reflecting method calls after the invoker called this method
    StackFrame callingStackFrame = getStackFrameWithOffset(2);
    return callingStackFrame!=null ? callingStackFrame.getMethodType() : null;
  }   
}
