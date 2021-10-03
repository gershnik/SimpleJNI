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

package smjni.jnigen.ksp

internal class UniqueName(val original: String, val colliding: String, private val value: String) {

    override fun toString(): String {
        return value
    }
}

internal class NameTable {

    private val nameCounts = mutableMapOf<String, Int>()

    companion object {

        val reservedNames =
            setOf("alignas", "alignof", "and", "and_eq", "asm", "atomic_cancel", "atomic_commit", "atomic_noexcept",
                "auto", "bitand", "bitor", "bool", "break", "case", "catch", "char", "char8_t", "char16_t", "char32_t",
                "class", "compl", "concept", "const", "consteval", "constexpr", "constinit", "const_cast", "continue",
                "co_await", "co_return", "co_yield", "decltype", "default", "delete", "do", "double", "dynamic_cast",
                "else", "enum", "explicit", "export", "extern", "false", "final", "float", "for", "friend", "goto", "if",
                "import", "inline", "int", "long", "module", "mutable", "namespace", "new", "noexcept", "not", "not_eq",
                "nullptr", "operator", "or", "or_eq", "override", "private", "protected", "public", "reflexpr", "register",
                "reinterpret_cast", "requires", "return", "short", "signed", "sizeof", "static", "static_assert",
                "static_cast", "struct", "switch", "synchronized", "template", "this", "thread_local", "throw",
                "transaction_safe", "transaction_safe_dynamic", "true", "try", "typedef", "typeid", "typename", "union",
                "unsigned", "using", "virtual", "void", "volatile", "wchar_t", "while", "xor", "xor_eq")

        val validNamePattern = Regex("[_a-zA-Z][_a-zA-Z0-9]*")
        val invalidFirstLetterPattern = Regex("[^_a-zA-Z]")
        val invalidSubsequentLetterPattern = Regex("[^_a-zA-Z0-9]")
    }

    internal fun allocateName(name: String): UniqueName {
        val sanitized = if (reservedNames.contains(name))
            name.replaceFirstChar { it + "_" }
        else if (name.isEmpty())
            "_"
        else
            name.takeIf { it.matches(validNamePattern)} ?: (
                    name.substring(0, 1).replace(invalidFirstLetterPattern, "_") +
                            if (name.length > 1)
                                name.substring(1).replace(invalidSubsequentLetterPattern, "_")
                            else "")

        val count = nameCounts.merge(sanitized, 1, Int::plus)!!
        return UniqueName(name, sanitized,sanitized + if (count > 1) "${count - 1}" else "")
    }

}