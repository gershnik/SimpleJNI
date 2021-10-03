/*
 Copyright 2021 SmJNI Contributors

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

package smjni.jnigen.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSDeclaration

class LexicographicallyComparableWrapper<T: Comparable<T>>(val list: List<T>) :
        Comparable<LexicographicallyComparableWrapper<T>> {

    override fun compareTo(other: LexicographicallyComparableWrapper<T>): Int {
        for (i in 0 until this.list.size.coerceAtMost(other.list.size)) {
            compareValues(this.list[i], other.list[i]).let { res -> if (res != 0) return res }
        }
        return this.list.size.compareTo(other.list.size)
    }
}

class LexicographicallyComparableWrapperWithComparator<T>(val list: List<T>, val comp: Comparator<T>) :
        Comparable<LexicographicallyComparableWrapperWithComparator<T>> {

    override fun compareTo(other: LexicographicallyComparableWrapperWithComparator<T>): Int {
        for (i in 0 until this.list.size.coerceAtMost(other.list.size)) {
            comp.compare(this.list[i], other.list[i]).let { res -> if (res != 0) return res }
        }
        return this.list.size.compareTo(other.list.size)
    }
}

fun <T: Comparable<T>> lexicographicallyComparable(list: List<T>) : Comparable<LexicographicallyComparableWrapper<T>>  {

    return LexicographicallyComparableWrapper(list)
}

fun <T> lexicographicallyComparable(list: List<T>, comp: Comparator<T>) : Comparable<LexicographicallyComparableWrapperWithComparator<T>>  {

    return LexicographicallyComparableWrapperWithComparator(list, comp)
}

@KspExperimental
fun Resolver.getJavaNameOfKotlinType(declaration: KSDeclaration): String {
    val qualifiedName = declaration.qualifiedName
    if (qualifiedName != null) {
        val mapped = this.mapKotlinNameToJava(qualifiedName)
        if (mapped != null)
            return mapped.asString()
        val packageName = declaration.packageName.asString()
        if (packageName.isNotEmpty()) {
            val unqualifiedName = qualifiedName.asString()
                .removePrefix("$packageName.")
                .replace(".", "$")
            return "$packageName.$unqualifiedName"
        }
        return qualifiedName.asString().replace(".", "$")
    } else {
        return declaration.simpleName.asString().replace(".", "$")
    }
}


