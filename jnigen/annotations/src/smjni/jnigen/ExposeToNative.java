/*
 Copyright 2014 Smartsheet Inc.
 Copyright 2019 SmJNI Contributors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package smjni.jnigen;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Marks a class element that will be made accessible from native code
 *
 * Putting this annotation on a class will make JniGen processor to
 * <ul>
 * <li>Define a C++ strong type (e.g. jSomeClass) to represent objects
 * of annotated class in C++ code</li>
 * <li>Define a C++ class (derived from java_class&lt;jSomeClass&gt;) to
 * access annotated class in C++ code</li>
 * <li>Make any elements annotated with {@link CalledByNative}  accessible
 *   via C++ class</li>
 * <li>Generate declarations for any `native` methods in C++ class to be
 * implemented in native code</li>
 * </ul>
 *
 * The parameters of this annotation allow you to customize code generation process
 */
@Target(value = {TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface ExposeToNative {

    /**
     * The "stem" of the name to be used for generated C++ types
     * <p>
     * For example if value is "Foo" the generated objected type will be jFoo,
     * the C++ class Foo_class etc.
     */
    String value() default "";

    /**
     * The name of the C++ strong type to represent instances of the class
     * <p>
     * The default is jJavaTypeName or if {@link #value()} is set j{$value}
     */
    String typeName() default "";

    /**
     * The name of the C++ type to represent the class
     * <p>
     * The default is JavaTypeName_class or if {@link #value()} is set {$value}_class
     */
    String className() default "";

    /**
     * Name of the header file for the generated C++ code
     * <p>
     * The default is JavaTypeName_class.h or if {@link #value()} is set {$value}_class.h
     */
    String header() default "";
}
