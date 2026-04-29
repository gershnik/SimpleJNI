# SimpleJNI library #

[![Language](https://img.shields.io/badge/language-C++-blue.svg)](https://isocpp.org/)
[![Standard](https://img.shields.io/badge/C%2B%2B-17-blue.svg)](https://en.wikipedia.org/wiki/C%2B%2B#Standardization)
[![License](https://img.shields.io/badge/license-Apache%202-brightgreen.svg)](https://opensource.org/license/apache-2-0/)
[![Tests](https://github.com/gershnik/SimpleJNI/actions/workflows/test.yml/badge.svg)](https://github.com/gershnik/SimpleJNI/actions/workflows/test.yml)


A powerful lightweight C++ wrapper for JNI

**Note**: this library is based on SmJNI (https://github.com/smartsheet-mobile/smjni) developed by the same author. The SmJNI library is no longer actively maintained and all further enhancements will happen here. 

## Purpose

Writing C++ code for JNI is hard, error-prone and unsafe. The purpose of this library is to make it easy and safe.
It targets two main scenarios:

1. Call Java code from C++
2. Implement native Java methods in C++

The approach this library is taking is different from SWIG and JNA. Unlike either, it does not attempt to easily
expose existing C or C++ code to Java. Trying to do so is, in my opinion, rarely a good idea. Instead it does
exactly the opposite: easily exposes Java to C++.
With this library, if you want to simply connect existing C++ code to Java, you will need to write your own wrappers.
However, doing so will become easy and straightforward. As will be more sophisticated things like implementing
classes partially natively or using Java from within a C++ application.

### Goals

*    Never have to figure out things like `([BLjava/lang/String;)Ljava/lang/Throwable;`
*    Never have to call vararg (...) functions hoping that you got the arguments right. And debug weird behavior when you didn't...
*    Type safety: never have to worry "is this `jobject` what I think it is?"
*    RAII wrappers for anything that has do/undo semantics
*    Proper error handling. Use exceptions instead of error-prone and ill-defined manual checks.
     If a Java call threw an exception, it should automatically become a C++ exception. You should also be able to easily
     convert it back to Java when returning from a JNI call.
*    Dealing with JNI local/global reference stuff should be safe and easy by default.
*    It should be possible to access Java arrays via an STL collection interface.
*    You should never have to deal with *modified UTF-8*. All string operations should either use UTF-16 or standard UTF-8
*    It should be possible to get the JNIEnv from arbitrary C++ code without the trouble of passing it through every call.
*    You shouldn't need to find Java classes again and again every time you want to use them.
*    All of the above should be accomplished without sacrificing performance compared to "raw" JNI use. However, if there
     is an unavoidable choice between marginal performance gain and correctness/safety, the latter should win.
*    All of the JNI functionality required to write correct code should be available in the library. There should never be a need
     to manually invoke `JNIEnv` methods. Conversely, dangerous (e.g. `ThrowNew`) and unnecessary JNI methods should not
     be exposed.
*    Modularity: it should be possible to mix and match parts of the library with hand written JNI code if desired. It should not
     force an all-or-nothing approach.

### Non-Goals

*    Automatically expose C or C++ code to Java
*    Completely hide JNI fundamentals: existence of per-thread `JNIEnv *`, global/local reference semantics, etc. from C++ code
*    Provide C++ wrappers for common Java classes
*    Provide Java wrappers for common C++ classes
*    Support old C++ compilers and libraries. This library requires C++17.

## Building

Instructions on how to build SimpleJNI are available on the project [wiki](https://github.com/gershnik/SimpleJNI/wiki/Building)

## User's Guide

User's guide (work in progress) is also available on the [wiki](https://github.com/gershnik/SimpleJNI/wiki/User%27s-Guide)

