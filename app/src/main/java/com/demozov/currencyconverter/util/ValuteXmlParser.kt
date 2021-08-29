package com.demozov.currencyconverter.util

import android.util.Xml
import com.demozov.currencyconverter.pojo.Valute
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

private val ns: String? = null

class ValuteXmlParser {

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<Valute> {
        inputStream.use { stream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(stream, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<Valute> {
        val valutes = mutableListOf<Valute>()
        parser.require(XmlPullParser.START_TAG, ns, "ValCurs")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "Valute") {
                valutes.add(readValute(parser))
            } else {
                skip(parser)
            }
        }
        return valutes
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readValute(parser: XmlPullParser): Valute {
        parser.require(XmlPullParser.START_TAG, ns, "Valute")
        var numCode: String? = null
        var charCode: String? = null
        var nominal: String? = null
        var name: String? = null
        var value: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "NumCode" -> numCode = readValue(parser, "NumCode")
                "CharCode" -> charCode = readValue(parser, "CharCode")
                "Nominal" -> nominal = readValue(parser, "Nominal")
                "Name" -> name = readValue(parser, "Name")
                "Value" -> value = readValue(parser, "Value")
                else -> skip(parser)
            }
        }
        return Valute(numCode, charCode, nominal, name, value)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readValue(parser: XmlPullParser, tag: String): String {
        parser.require(XmlPullParser.START_TAG, ns, tag)
        val value = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, tag)
        return value
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}