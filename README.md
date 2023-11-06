# SimpleJNI library #

[![Language](https://img.shields.io/badge/language-C++-blue.svg)](https://isocpp.org/)
[![Standard](https://img.shields.io/badge/C%2B%2B-17-blue.svg)](https://en.wikipedia.org/wiki/C%2B%2B#Standardization)
[![License](https://img.shields.io/badge/license-Apache%202-brightgreen.svg)](https://opensource.org/license/apache-2-0/)
![Android](https://img.shields.io/badge/Android-F5F5F5?style=flat&logo=android&logoColor=green)
![JVM](https://img.shields.io/badge/JVM-F5F5F5?style=flat&logo=data:image/svg%2bxml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2NCIgaGVpZ2h0PSI2NCIgdmlld0JveD0iMCAwIDMyIDMyIj48cGF0aCBkPSJNMTEuNjIyIDI0Ljc0cy0xLjIzLjc0OC44NTUuOTYyYzIuNTEuMzIgMy44NDcuMjY3IDYuNjI1LS4yNjdhMTAuMDIgMTAuMDIgMCAwIDAgMS43NjMuODU1Yy02LjI1IDIuNjcyLTE0LjE2LS4xNi05LjI0NC0xLjU1em0tLjgtMy40NzNzLTEuMzM2IDEuMDE1Ljc0OCAxLjIzYzIuNzI1LjI2NyA0Ljg2Mi4zMiA4LjU1LS40MjdhMy4yNiAzLjI2IDAgMCAwIDEuMjgyLjgwMWMtNy41MzQgMi4yNDQtMTUuOTc2LjIxNC0xMC41OC0xLjYwM3ptMTQuNzQ3IDYuMDlzLjkwOC43NDgtMS4wMTUgMS4zMzZjLTMuNTggMS4wNy0xNS4wMTQgMS4zOS0xOC4yMiAwLTEuMTIyLS40OCAxLjAxNS0xLjE3NSAxLjctMS4yODIuNjk1LS4xNiAxLjA3LS4xNiAxLjA3LS4xNi0xLjIzLS44NTUtOC4xNzUgMS43NjMtMy41MjYgMi41MSAxMi43NyAyLjA4NCAyMy4yOTYtLjkwOCAxOS45ODMtMi40MDR6TTEyLjIgMTcuNjMzcy01LjgyNCAxLjM5LTIuMDg0IDEuODdjMS42MDMuMjE0IDQuNzU1LjE2IDcuNjk0LS4wNTMgMi40MDQtLjIxNCA0LjgxLS42NCA0LjgxLS42NHMtLjg1NS4zNzQtMS40NDMuNzQ4Yy01LjkzIDEuNTUtMTcuMzEyLjg1NS0xNC4wNTItLjc0OCAyLjc3OC0xLjMzNiA1LjA3Ni0xLjE3NSA1LjA3Ni0xLjE3NXptMTAuNDIgNS44MjRjNS45ODQtMy4xIDMuMjA2LTYuMDkgMS4yODItNS43MTctLjQ4LjEwNy0uNjk1LjIxNC0uNjk1LjIxNHMuMTYtLjMyLjUzNC0uNDI3YzMuNzk0LTEuMzM2IDYuNzg2IDQuMDA3LTEuMjMgNi4wOSAwIDAgLjA1My0uMDUzLjEwNy0uMTZ6bS05LjgzIDguNDQyYzUuNzcuMzc0IDE0LjU4Ny0uMjE0IDE0LjgtMi45NCAwIDAtLjQyNyAxLjA3LTQuNzU1IDEuODctNC45MTYuOTA4LTExLjAwNy44LTE0LjU4Ny4yMTQgMCAwIC43NDguNjQgNC41NDIuODU1eiIgZmlsbD0iIzRlNzg5NiIvPjxwYXRoIGQ9Ik0xOC45OTYuMDAxczMuMzEzIDMuMzY2LTMuMTUyIDguNDQyYy01LjE4MyA0LjExNC0xLjE3NSA2LjQ2NSAwIDkuMTM3LTMuMDQ2LTIuNzI1LTUuMjM2LTUuMTMtMy43NC03LjM3M0MxNC4yOTQgNi44OTMgMjAuMzMyIDUuMyAxOC45OTYuMDAxem0tMS43IDE1LjMzNWMxLjU1IDEuNzYzLS40MjcgMy4zNjYtLjQyNyAzLjM2NnMzLjk1NC0yLjAzIDIuMTM3LTQuNTQyYy0xLjY1Ni0yLjQwNC0yLjk0LTMuNTggNC4wMDctNy41ODcgMCAwLTEwLjk1MyAyLjcyNS01LjcxNyA4Ljc2M3oiIGZpbGw9IiNmNTgyMTkiLz48L3N2Zz4=)
[![Tests](https://github.com/gershnik/SimpleJNI/actions/workflows/test.yml/badge.svg)](https://github.com/gershnik/SimpleJNI/actions/workflows/test.yml)


A powerful lightweight C++ wrapper for JNI

**Note**: this library is based on SmJNI (https://github.com/smartsheet-mobile/smjni) developed by the same author. The SmJNI library is no longer actively maintained and all further enhancements will happen here. 

## Purpose

Writing C++ code for JNI is hard, error prone and unsafe. The purpose of this library is to make it easy and safe.
It targets two main scenarios

1. Call Java code from C++
2. Implement native Java methods in C++

The approach this library is taking is different from SWIG and JNA. Unlike either it does not attempt to easily
expose existing C or C++ code to Java. Trying to do so is, in our opinion, rarely a good idea. Instead it does
exactly the opposite: easily exposes Java to C++.
With this library if you want to simply connect existing C++ code to Java you will need to write your own wrappers.
However, doing so will become easy and straightforward. As will be more sophisticated things like implementing
classes partially natively or using Java from within C++ application.

### Goals

*    Never have to figure out things like `([BLjava/lang/String;)Ljava/lang/Throwable;`
*    Never have to call vararg (...) functions hoping that you got the arguments right. And debug weird behavior when you didn't...
*    Type safety: never have to worry "is this `jobject` what I think it is?"
*    RAII wrappers for anything that has do/undo semantics
*    Proper error handling. Use exceptions instead of error-prone and ill-defined manual checks.
     If Java call threw an exception it should automatically become C++ exception. You should also be able to easily
     convert it back to Java when returning from JNI call. 
*    Dealing with JNI local/global reference stuff should be safe and easy by default. 
*    It should be possible to access Java arrays via STL collection interface. 
*    You should never have to deal with *modified UTF-8*. All string operations should either use UTF-16 or standard UTF-8
*    It should be possible to get the JNIEnv in arbitrary C++ code without the trouble of passing it through every call.
*    You shouldn't need to find Java classes again and again every time you want to use them.
*    All of the above should be accomplished without sacrificing performance compared to "raw" JNI use. However, if there
     is an unavoidable choice between marginal performance gain and correctness/safety the later should win.
*    All of the JNI functionality required to write correct code should be available in the library. There should never be a need
     to manually invoke JNIEnv methods. Conversely dangerous (e.g. `ThrowNew`) and unnecessary JNI methods should not
     be exposed.
*    Modularity: it should be possible to mix and match parts of library with hand written JNI code if desired. It should not
     force all or nothing approach.

### Non-Goals

*    Automatically expose C or C++ code to Java
*    Completely hide JNI fundamentals: existence of per thread `JNIEnv *`, global/local reference semantics etc. from C++ code
*    Provide C++ wrappers for common Java classes
*    Provide Java wrappers for common C++ classes
*    Support old C++ compilers and libraries. This library requires C++17. 

## Building

Instructions on how to build SimpleJNI are available on project [wiki](https://github.com/gershnik/SimpleJNI/wiki/Building)

## User's Guide

User's guide (work in progress) is also available on [wiki](https://github.com/gershnik/SimpleJNI/wiki/User%27s-Guide)

