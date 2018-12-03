/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 * @author Fernando Cejas (the android10 coder)
 */
package org.android10.gintonic.aspect;

import android.app.Activity;
import android.app.Dialog;

import org.android10.gintonic.internal.DebugLog;
import org.android10.gintonic.internal.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Aspect representing the cross cutting-concern: Method and Constructor Tracing.
 */
@Aspect
public class TraceAspect {

  private static final String TAG = "TraceAspect";

  //进行类似于正则表达式的匹配，被匹配到的方法都会被截获
  //截获任何包中被注解的普通方法
  private static final String POINTCUT_METHOD_EXEC_BEFORE =
          "execution(@org.android10.gintonic.annotation.DebugBefore * *(..))";
  private static final String POINTCUT_METHOD_EXEC_AFTER =
          "execution(@org.android10.gintonic.annotation.DebugAfter * *(..))";
  private static final String POINTCUT_METHOD_EXEC_AROUND =
          "execution(@org.android10.gintonic.annotation.DebugAround * *(..))";
  private static final String POINTCUT_METHOD_CALL_BEFORE =
          "call(@org.android10.gintonic.annotation.DebugBefore * *(..))";
  private static final String POINTCUT_METHOD_CALL_AFTER =
          "call(@org.android10.gintonic.annotation.DebugAfter * *(..))";
  private static final String POINTCUT_METHOD_CALL_AROUND =
          "call(@org.android10.gintonic.annotation.DebugAround * *(..))";
  //截获任何包中被DebugTrace注解的构造器方法
  private static final String POINTCUT_CONSTRUCTOR =
      "execution(@org.android10.gintonic.annotation.DebugTrace *.new(..))";

  //截获任何包中以类名以RelativeLayoutTestActivity结尾的类的onCreate方法
  private static final String POINTCUT_METHOD_ACTIVITY =
          "execution(* *..RelativeLayoutTestActivity+.onCreate(..))";

  //切点，ajc会将切点对应的Advise编织入目标程序当中
  @Pointcut(POINTCUT_METHOD_EXEC_BEFORE)
  public void methodExecBefore() {}
  @Pointcut(POINTCUT_METHOD_EXEC_AFTER)
  public void methodExecAfter() {}
  @Pointcut(POINTCUT_METHOD_EXEC_AROUND)
  public void methodExecAround() {}
  @Pointcut(POINTCUT_METHOD_CALL_BEFORE)
  public void methodCallBefore() {}
  @Pointcut(POINTCUT_METHOD_CALL_AFTER)
  public void methodCallAfter() {}

  /**
   * 在截获的目标方法调用之前执行该Advise
   * @param joinPoint
   */
  @Before("methodExecBefore()")
  public void before(JoinPoint joinPoint) {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String className = methodSignature.getDeclaringType().getSimpleName();
    String methodName = methodSignature.getName();
    Activity activity = null;
    //获取目标对象
    Object object = joinPoint.getTarget();
    if (object instanceof Activity) {
      activity = ((Activity) object);
      //插入自己的实现，控制目标对象的执行
      Dialog dialog = new Dialog(activity);
      dialog.show();
    }

    //做其他的操作
    DebugLog.log(TAG, methodName + ": before");
  }
  /**
   * 在截获的目标方法调用返回之后（无论正常还是异常）执行该Advise
   * @param joinPoint
   */
  @After("methodExecAfter()")
  public void after(JoinPoint joinPoint) {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String className = methodSignature.getDeclaringType().getSimpleName();
    String methodName = methodSignature.getName();
    DebugLog.log(TAG, methodName + ": after");
  }

  @Around("methodExecAround()")
  public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String className = methodSignature.getDeclaringType().getSimpleName();
    String methodName = methodSignature.getName();

    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    // 执行该Advice要替换的方法
    Object result = joinPoint.proceed();
    stopWatch.stop();

    DebugLog.log(TAG, buildLogMessage(methodName, stopWatch.getTotalTimeMillis()));

    return result;
  }

  /**
   * Create a log message.
   *
   * @param methodName A string with the method name.
   * @param methodDuration Duration of the method in milliseconds.
   * @return A string representing message.
   */
  private static String buildLogMessage(String methodName, long methodDuration) {
    StringBuilder message = new StringBuilder();
    message.append(TAG + ": ");
    message.append(methodName);
    message.append(" --> ");
    message.append("[");
    message.append(methodDuration);
    message.append("ms");
    message.append("]");

    return message.toString();
  }
}
