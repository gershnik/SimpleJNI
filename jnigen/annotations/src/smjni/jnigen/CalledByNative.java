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

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Marks class element that will be made callable by native code
 *
 * Putting this annotation on a class element will cause JniGen
 * annotation processor to emit bridging C++ code to access the
 * element from native code.
 */
@Target(value = {METHOD, CONSTRUCTOR, FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface CalledByNative
{
    /**
     * Make a non-static method also callable non-virtually
     * <p>
     * By default, JniGen only exposes normal virtual calls
     * to class methods. If this argument is set to true JniGen
     * will generate additional wrappers to allow non-virtual
     * call.
     * This has no effect of constructors, fields and static methods
     */
    boolean allowNonVirtualCall() default false;
}