/*
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

#include <smjni/smjni.h>

#include <doctest.h>

#include <iterator>
#include <string>

using namespace smjni;

static std::u16string convert(const char * utf8)
{
    std::u16string ret;
    utf8_to_utf16(utf8, utf8 + std::char_traits<char>::length(utf8), std::back_inserter(ret));
    return ret;
}

#if __cpp_char8_t >= 201811L
    using std_u8string = std::u8string;
#else
    using std_u8string = std::string;
#endif

static std_u8string convert(const char16_t * utf16)
{
    std_u8string ret;
    utf16_to_utf8(utf16, utf16 + std::char_traits<char16_t>::length(utf16), std::back_inserter(ret));
    return ret;
}

static std::u32string convert32(const char16_t * utf16)
{
    std::u32string ret;
    utf16_to_utf32(utf16, utf16 + std::char_traits<char16_t>::length(utf16), std::back_inserter(ret));
    return ret;
}

static std::u16string convert32(const char32_t * utf32)
{
    std::u16string ret;
    utf32_to_utf16(utf32, utf32 + std::char_traits<char32_t>::length(utf32), std::back_inserter(ret));
    return ret;
}

TEST_SUITE_BEGIN("utf8");

TEST_CASE( "utf8 to utf16" )
{
    //Adapted from https://www.w3.org/2001/06/utf-8-wrong/UTF-8-test.html
        
    //1  Some correct UTF-8 text
    CHECK(convert("κόσμε") == u"κόσμε");

    //2  Boundary condition test cases
        
    //2.1  First possible sequence of a certain length
    CHECK(convert("\xC2\x80") == u"\u0080");
    CHECK(convert("\xE0\xA0\x80") == u"ࠀ");
    CHECK(convert("\xF0\x90\x80\x80") == u"𐀀");
    CHECK(convert("\xF8\x88\x80\x80\x80") == u"�����");
    CHECK(convert("\xFC\x84\x80\x80\x80\x80") == u"������");
    
    //2.2  Last possible sequence of a certain length
    CHECK(convert("\x7F") == u"\u007F");
    CHECK(convert("\xDF\xBF") == u"\u07FF");
    CHECK(convert("\xEF\xBF\xBF") == u"\uFFFF");
    CHECK(convert("\xF7\xBF\xBF\xBF") == u"����");
    CHECK(convert("\xFB\xBF\xBF\xBF\xBF") == u"�����");
    CHECK(convert("\xFD\xBF\xBF\xBF\xBF\xBF") == u"������");
    
    //2.3  Other boundary conditions
    CHECK(convert("\xED\x9F\xBF") == u"\uD7FF");
    CHECK(convert("\xEE\x80\x80") == u"\uE000");
    CHECK(convert("\xEF\xBF\xBD") == u"�");
    CHECK(convert("\xF4\x8F\xBF\xBF") == u"\U0010FFFF");
    CHECK(convert("\xF4\x90\x80\x80") == u"����");
    
    //3  Malformed sequences
    
    //3.1  Unexpected continuation bytes
    CHECK(convert("\x80") == u"�");
    CHECK(convert("\xBF") == u"�");
    CHECK(convert("\x80\xBF") == u"��");
    CHECK(convert("\x80\xBF\x80") == u"���");
    CHECK(convert("\x80\xBF\x80\xBF") == u"����");
    CHECK(convert("\x80\x81\x82\x83\x84\x85\x86\x87\x88\x89\x8A\x8B\x8C\x8D\x8E\x8F"
                  "\x90\x91\x92\x93\x94\x95\x96\x97\x98\x99\x9A\x9B\x9C\x9D\x9E\x9F"
                  "\xA0\xA1\xA2\xA3\xA4\xA5\xA6\xA7\xA8\xA9\xAA\xAB\xAC\xAD\xAE\xAF"
                  "\xB0\xB1\xB2\xB3\xB4\xB5\xB6\xB7\xB8\xB9\xBA\xBB\xBC\xBD\xBE\xBF")
          == u"����������������"
             u"����������������"
             u"����������������"
             u"����������������");
    
    //3.2  Lonely start characters
    CHECK(convert("\xC0 \xC1 \xC2 \xC3 \xC4 \xC5 \xC6 \xC7 \xC8 \xC9 \xCA \xCB \xCC \xCD \xCE \xCF "
                  "\xD0 \xD1 \xD2 \xD3 \xD4 \xD5 \xD6 \xD7 \xD8 \xD9 \xDA \xDB \xDC \xDD \xDE \xDF ")
        == u"� � � � � � � � � � � � � � � � "
           u"� � � � � � � � � � � � � � � � ");
    CHECK(convert("\xE0 \xE1 \xE2 \xE3 \xE4 \xE5 \xE6 \xE7 \xE8 \xE9 \xEA \xEB \xEC \xED \xEE \xEF ")
          == u"� � � � � � � � � � � � � � � � ");
    CHECK(convert("\xF0 \xF1 \xF2 \xF3 \xF4 \xF5 \xF6 \xF7 ") == u"� � � � � � � � ");
    CHECK(convert("\xF8 \xF9 \xFA \xFB ") == u"� � � � ");
    CHECK(convert("\xFC \xFD ") == u"� � ");
    
    //3.3  Sequences with last continuation byte missing
    CHECK(convert("\xC0") == u"�");
    CHECK(convert("\xE0\x80") == u"��");
    CHECK(convert("\xF0\x80\x80") == u"���");
    CHECK(convert("\xDF") == u"�");
    CHECK(convert("\xEF\xBF") == u"�"); //Safari 11.0.1 (as of 11/24/2017 gets this one wrong, Chrome and FF are right
    CHECK(convert("\xF7\xBF\xBF") == u"���");
    
    //3.4  Concatenation of incomplete sequences
    CHECK(convert("\xC0\xE0\x80\xF0\x80\x80\xDF\xEF\xBF\xF7\xBF\xBF") == u"�����������");
    
    //3.5  Impossible bytes
    CHECK(convert("\xFE") == u"�");
    CHECK(convert("\xFF") == u"�");
    CHECK(convert("\xFE\xFE\xFF\xFF") == u"����");
    
    //4  Overlong sequences
    
    //4.1  Examples of an overlong ASCII character
    CHECK(convert("\xC0\xAF") == u"��");
    CHECK(convert("\xE0\x80\xAF") == u"���");
    CHECK(convert("\xF0\x80\x80\xAF") == u"����");
    
    //4.2  Maximum overlong sequences
    CHECK(convert("\xC1\xBF") == u"��");
    CHECK(convert("\xE0\x9F\xBF") == u"���");
    CHECK(convert("\xF0\x8F\xBF\xBF") == u"����");
    
    //4.3  Overlong representation of the NUL character
    CHECK(convert("\xC0\x80") == u"��");
    CHECK(convert("\xE0\x80\x80") == u"���");
    CHECK(convert("\xF0\x80\x80\x80") == u"����");
    
    //5  Illegal code positions
    CHECK(convert("\xED\xA0\x80") == u"���");
    CHECK(convert("\xED\xAD\xBF") == u"���");
    CHECK(convert("\xED\xAE\x80") == u"���");
    CHECK(convert("\xED\xAF\xBF") == u"���");
    CHECK(convert("\xED\xB0\x80") == u"���");
    CHECK(convert("\xED\xBE\x80") == u"���");
    CHECK(convert("\xED\xBF\xBF") == u"���");
    
    //5.2 Paired UTF-16 surrogates
    CHECK(convert("\xED\xA0\x80\xED\xB0\x80") == u"������");
    CHECK(convert("\xED\xA0\x80\xED\xBF\xBF") == u"������");
    CHECK(convert("\xED\xAD\xBF\xED\xB0\x80") == u"������");
    CHECK(convert("\xED\xAD\xBF\xED\xBF\xBF") == u"������");
    CHECK(convert("\xED\xAE\x80\xED\xB0\x80") == u"������");
    CHECK(convert("\xED\xAE\x80\xED\xBF\xBF") == u"������");
    CHECK(convert("\xED\xAF\xBF\xED\xB0\x80") == u"������");
    CHECK(convert("\xED\xAF\xBF\xED\xBF\xBF") == u"������");
    
    //5.3 Other illegal code positions
    CHECK(convert("\xEF\xBF\xBE") == u"\uFFFE");
    CHECK(convert("\xEF\xBF\xBF") == u"\uFFFF");
    
    //From https://www.unicode.org/versions/Unicode10.0.0/ch03.pdf
    CHECK(convert("\x61\xF1\x80\x80\xE1\x80\xC2\x62\x80\x63\x80\xBF\x64") == u"a���b�c��d");

}

TEST_CASE( "utf16 to utf8" )
{
    CHECK(convert(u"κόσμε") == u8"κόσμε");
    CHECK(convert(u"\xD800") == u8"�");
    CHECK(convert(u"\xDBFF") == u8"�");
    CHECK(convert(u"\xDC00") == u8"�");
    CHECK(convert(u"\xDC00") == u8"�");
    CHECK(convert(u"\xDFFF") == u8"�");
    CHECK(convert(u"\xD800\xDBFF") == u8"��");
    CHECK(convert(u"\xD800\x0061") == u8"�a");
    CHECK(convert(u"\xDBFF\xDBFF") == u8"��");
    CHECK(convert(u"\xDBFF\x0061") == u8"�a");
    CHECK(convert(u"\xD800\xDC00") == u8"𐀀");
    CHECK(convert(u"\xD800\xDFFF") == u8"\U000103FF");
    CHECK(convert(u"\xDBFF\xDC00") == u8"\U0010FC00");
    CHECK(convert(u"\xDBFF\xDFFF") == u8"\U0010FFFF");
}

TEST_CASE( "utf16 to utf32" )
{
    CHECK(convert32(u"κόσμε") == U"κόσμε");
    CHECK(convert32(u"\xD800") == U"�");
    CHECK(convert32(u"\xDBFF") == U"�");
    CHECK(convert32(u"\xDC00") == U"�");
    CHECK(convert32(u"\xDC00") == U"�");
    CHECK(convert32(u"\xDFFF") == U"�");
    CHECK(convert32(u"\xD800\xDBFF") == U"��");
    CHECK(convert32(u"\xD800\x0061") == U"�a");
    CHECK(convert32(u"\xDBFF\xDBFF") == U"��");
    CHECK(convert32(u"\xDBFF\x0061") == U"�a");
    CHECK(convert32(u"\xD800\xDC00") == U"𐀀");
    CHECK(convert32(u"\xD800\xDFFF") == U"\U000103FF");
    CHECK(convert32(u"\xDBFF\xDC00") == U"\U0010FC00");
    CHECK(convert32(u"\xDBFF\xDFFF") == U"\U0010FFFF");
}

TEST_CASE( "utf32 to utf16" )
{
    CHECK(convert32(U"κόσμε") == u"κόσμε");
    CHECK(convert32(U"𐀀") == u"\xD800\xDC00");
    CHECK(convert32(U"\U000103FF") == u"\xD800\xDFFF");
    CHECK(convert32(U"\U0010FC00") == u"\xDBFF\xDC00");
    CHECK(convert32(U"\U0010FFFF") == u"\xDBFF\xDFFF");
}

TEST_SUITE_END();