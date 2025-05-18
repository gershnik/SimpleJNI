# Changelog
All notable changes to this project will be documented in this file.

## Unreleased

### Native Code
* Added missing header when compiling with GCC is C++23 mode
* CMake minimum required version is now 3.10

### Annotation processors
* Updated dependencies
* KSP code generator is now built with KSP 2.1.21-2.0.1. This *seems*
  to work fine with older Kotlin/KSP versions but if you experience any 
  issues upgrading KSP might be necessary.

## [3.12] - 2025-02-16

### Native Code
* CMake build now support installation (`cmake --install ...`)
* Test targets are no longer part of default ('all') target
* There is a new CMake option `SMJNI_NO_TESTS` that suppresses all test related functionality

### Annotation processors
* Kotlin version required updated to 1.9.25
* KSP code generator now requires KSP version 1.9.25-1.0.20

## [3.11] - 2024-08-30

### Native Code
* `java_string_create` now has overloads that accept `char8_t *`, `(char8_t *, size_t)`, `char16_t *`, `(char16_t *, size_t)` and, under C++20, any contiguous range of these types (e.g. `std::vector`, `std::string_view`, `std::span` etc.)
* `java_string_access` is now movable
* `java_string_access` gained more vector-like methods: `cbegin`, `cend`, `rbegin`, `rend`, `crbegin`, `crend`, `data`, `empty`, `at`, `front` and `back`.   
* `java_array_access` is now properly movable
* `java_array_access::at()` is no longer erroneously marked `noexcept`
* Under C++20, `java_array_access` now properly satisfies `std::ranges::contiguous_range` concept for arrays of primitive types and `std::ranges::random_access_range` for arrays of objects.
* Various bug fixes for `java_array_access` iterator methods
* Under C++20, `java_array_create` for primitive array types now accepts any input range as source
* Under C++20, `java_array_get_region` and `java_array_get_region` now accept any contiguous ranges as source/destination

### Annotation processors
* Kotlin version required updated to 1.9.24
* KSP code generator now requires KSP version 1.9.24-1.0.20

## [3.10] - 2024-05-09

### Native Code
* `java_string_create(JNIEnv *, const char *, size_t)` exposed publicly (thank you @JonathanLennox)
* Reduced amount of allocations during exception message extraction
* Reduced amount and complexity of template instanciations during compilation
* `java_frame` is now movable
* Headers now correctly check for C++17 rather than C++14

### Annotation processors
* No changes

## [3.9] - 2024-05-03

### Native Code
* No changes
### Annotation processors
* You can now use `@CalledByNative` annotation on Java/Kotlin `enum` members. 
* Kotlin version required updated to 1.9.23
* KSP code generator now requires KSP version 1.9.23-1.0.20

## [3.8] - 2023-11-06

### Native Code
* Added missing standard library includes not implicitly included on some compilers
* Modernized CMake file somewhat
### Annotation processors
* Kotlin version required updated to 1.9.0
* KSP code generator now requires KSP version 1.9.0-1.0.13


## [3.7] - 2023-04-01

### Native Code
* No changes
### Annotation processors
* Kotlin version required updated to 1.8.0
* KSP code generator now requires KSP version 1.8.0-1.0.9

## [3.6] - 2022-05-18

### Native Code
* GCC 12 compiler is now supported

### Annotation processors
* KSP Code Generator: KSP dependency updated to 1.6.21-1.0.5  
* Samples updated with latest Android/Kotlin/Gradle dependencies


## [3.5] - 2021-10-09

First release under a new name in this repo.

### Native code

* Small bug fix to `jstatic_cast` to allow more than one DEFINE_JAVA_CONVERSION for the same type

### JniGen generator

* JniGen generator is now on Maven Central instead of a private maven repo. 
* KSP annotation processor is now available in addition to Java/KAPT one.
* Amount of Gradle code needed to integrate JniGen has been greatly reduced 
* `/samples` directory now contain updated JniGen integration samples. 

See [Integration](https://github.com/gershnik/SimpleJNI/wiki/Integrating-JniGen) doc for more details about new Maven config and KSP processor

[3.5]: https://github.com/gershnik/SimpleJNI/releases/3.5
[3.6]: https://github.com/gershnik/SimpleJNI/releases/3.6
[3.7]: https://github.com/gershnik/SimpleJNI/releases/3.7
[3.8]: https://github.com/gershnik/SimpleJNI/releases/3.8
[3.9]: https://github.com/gershnik/SimpleJNI/releases/3.9
[3.10]: https://github.com/gershnik/SimpleJNI/releases/3.10
[3.11]: https://github.com/gershnik/SimpleJNI/releases/3.11
[3.12]: https://github.com/gershnik/SimpleJNI/releases/3.12
