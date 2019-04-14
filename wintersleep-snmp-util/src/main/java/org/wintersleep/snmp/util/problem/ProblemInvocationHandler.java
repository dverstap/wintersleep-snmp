/*
 * Copyright 2005 Davy Verstappen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wintersleep.snmp.util.problem;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.wintersleep.snmp.util.TextUtil;
import org.wintersleep.snmp.util.location.Location;
import org.wintersleep.snmp.util.problem.annotations.ProblemMethod;
import org.wintersleep.snmp.util.problem.annotations.ProblemProperty;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ProblemInvocationHandler <T> implements InvocationHandler {

    private ProblemEventHandler problemEventHandler;
    private Map<Method, MethodInvocationHandler> methodInvocationHandlerMap = new HashMap<Method, MethodInvocationHandler>();

    public ProblemInvocationHandler(Class<T> cl, ProblemEventHandler eh) {
        problemEventHandler = eh;

        for (Method method : cl.getDeclaredMethods()) {
            MethodInvocationHandler mih = new MethodInvocationHandler(method);
            methodInvocationHandlerMap.put(method, mih);
        }

        // TODO build up invocation data structure

        // TODO check all the methods for the annotations

        // TODO check all the parameter types: if they are not

        // question: we could always rely on the toString() method to print a value.
        // but then we cannot be sure to get a decent message. Maybe we can check if the
        // class overrides the toString method? If it doesn't, it surely won't give a decent
        // message.

        // TODO check that parameters are not declared as varargs: I don't think we can handle that.
    }


    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ((args == null || args.length == 0) && method.getName().equals("toString")) {
            return toString();
        }

        MethodInvocationHandler mih = methodInvocationHandlerMap.get(method);
        return mih.invoke(args);
    }

    private String getMethodName(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }


    class MethodInvocationHandler {
        private Method method;
        private ProblemMethod problemMethod;
        private String messageKey; // TODO
        private int locationParameterIndex = -1;

        public MethodInvocationHandler(Method method) {
            this.method = method;
            problemMethod = method.getAnnotation(ProblemMethod.class);
            if (problemMethod == null) {
                throw new IllegalArgumentException(getMethodName(method) + " is missing the " + ProblemMethod.class.getSimpleName() + " annotation");
            }
            // TODO determine messageKey

            check();
        }

        private void check() {
            Class[] paramTypes = method.getParameterTypes();
            Annotation[][] paramAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < paramAnnotations.length; i++) {
                ProblemProperty ep = getErrorParameter(paramAnnotations[i]);
                if (ep != null) {
                    Class paramType = paramTypes[i];
                    String propertyName = ep.value();
                    Method m = MethodUtils.getAccessibleMethod(paramType, "getOne" + TextUtil.ucFirst(propertyName), (Class[]) null);
                    if (m == null) {
                        m = MethodUtils.getAccessibleMethod(paramType, "is" + TextUtil.ucFirst(propertyName), (Class[]) null);
                    }
                    if (m == null) {
                        m = MethodUtils.getAccessibleMethod(paramType, propertyName, (Class[]) null);
                    }
                    if (m == null) {
                        throw new IllegalArgumentException(getMethodName(method) + ": parameter class does not have a property " + propertyName);
                    }
                }
            }


            for (int i = 0; i < paramTypes.length; i++) {
                Class c = paramTypes[i];
                if (Location.class.equals(c)) {
                    if (locationParameterIndex >= 0) {
                        //throw new IllegalArgumentException(getMethodName(method) + " has more than one " + Location.class.getSimpleName() + " parameter");
                    } else {
                        locationParameterIndex = i;
                    }
                }
            }

        }


        public Object invoke(Object[] args) {
            try {
                args = processParameters(args);

                Location location = null;
                if (locationParameterIndex >= 0) {
                    location = getLocation(args);
                    args = removeLocation(args);
                }

                ProblemEvent ev = new ProblemEvent(location, problemMethod.severity(), messageKey, problemMethod.message(), args);
                problemEventHandler.handle(ev);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        private Location getLocation(Object[] args) {
            if (locationParameterIndex >= 0) {
                return (Location) args[locationParameterIndex];
            }
            return null;
        }

        private Object[] removeLocation(Object[] args) {
            Object[] result = args;
            if (locationParameterIndex >= 0) {
                result = new Object[args.length - 1];
                for (int i = 0; i < locationParameterIndex; i++) {
                    result[i] = args[i];
                }
                for (int i = locationParameterIndex +1; i < args.length; i++) {
                    result[i-1] = args[i];
                }
            }
            return result;
        }

        private Object[] processParameters(Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
            Annotation[][] paramAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < paramAnnotations.length; i++) {
                ProblemProperty ep = getErrorParameter(paramAnnotations[i]);
                if (ep != null) {
                    Object arg = args[i];
                    String propertyName = ep.value();
                    Method m = MethodUtils.getAccessibleMethod(arg.getClass(), propertyName, (Class[]) null);
                    if (m != null) {
                        args[i] = m.invoke(arg);
                    } else {
                        args[i] = BeanUtils.getProperty(arg, propertyName);
                    }
                }
            }
            return args;
        }

        private ProblemProperty getErrorParameter(Annotation[] paramAnnotations) {
            for (Annotation annotation : paramAnnotations) {
                if (annotation instanceof ProblemProperty) {
                    return (ProblemProperty) annotation;
                }
            }
            return null;
        }


    }
}
