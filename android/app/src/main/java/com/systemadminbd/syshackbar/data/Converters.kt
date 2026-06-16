package com.systemadminbd.syshackbar.data

import android.util.Base64
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest

/** All conversion / encoding formats supported by the Encoder screen. */
enum class ConvertFormat(val label: String) {
    BASE64("Base64"),
    URL("URL"),
    HEX("Hex"),
    BINARY("Binary"),
    ASCII("Decimal"),
    HTML("HTML Entity"),
    ROT13("ROT13"),
    FROMCHARCODE("fromCharCode")
}

/** Pure, offline string converters. Each returns null on invalid input when decoding. */
object Converters {

    fun encode(input: String, format: ConvertFormat): String = when (format) {
        ConvertFormat.BASE64 -> Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        ConvertFormat.URL -> URLEncoder.encode(input, "UTF-8")
        ConvertFormat.HEX -> input.toByteArray(Charsets.UTF_8).joinToString("") { "%02x".format(it) }
        ConvertFormat.BINARY -> input.toByteArray(Charsets.UTF_8).joinToString(" ") {
            (it.toInt() and 0xFF).toString(2).padStart(8, '0')
        }
        ConvertFormat.ASCII -> input.map { it.code }.joinToString(" ")
        ConvertFormat.HTML -> input.map { c ->
            when (c) {
                '<' -> "&lt;"
                '>' -> "&gt;"
                '&' -> "&amp;"
                '"' -> "&quot;"
                '\'' -> "&#39;"
                else -> "&#${c.code};"
            }
        }.joinToString("")
        ConvertFormat.ROT13 -> rot13(input)
        ConvertFormat.FROMCHARCODE ->
            "String.fromCharCode(" + input.map { it.code }.joinToString(",") + ")"
    }

    fun decode(input: String, format: ConvertFormat): String? = try {
        when (format) {
            ConvertFormat.BASE64 ->
                String(Base64.decode(input.trim(), Base64.DEFAULT), Charsets.UTF_8)
            ConvertFormat.URL ->
                URLDecoder.decode(input, "UTF-8")
            ConvertFormat.HEX -> {
                val clean = input.replace(Regex("[\\s:]"), "").removePrefix("0x")
                if (clean.length % 2 != 0) null
                else String(ByteArray(clean.length / 2) {
                    clean.substring(it * 2, it * 2 + 2).toInt(16).toByte()
                }, Charsets.UTF_8)
            }
            ConvertFormat.BINARY -> {
                val parts = input.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
                String(ByteArray(parts.size) { parts[it].toInt(2).toByte() }, Charsets.UTF_8)
            }
            ConvertFormat.ASCII -> {
                val parts = input.trim().split(Regex("[\\s,]+")).filter { it.isNotEmpty() }
                parts.map { it.toInt().toChar() }.joinToString("")
            }
            ConvertFormat.HTML -> decodeHtml(input)
            ConvertFormat.ROT13 -> rot13(input)
            ConvertFormat.FROMCHARCODE -> {
                val nums = Regex("\\d+").findAll(input).map { it.value.toInt() }.toList()
                if (nums.isEmpty()) null else nums.map { it.toChar() }.joinToString("")
            }
        }
    } catch (e: Exception) {
        null
    }

    /** ROT13 — symmetric letter rotation (its own inverse). */
    private fun rot13(input: String): String = input.map { c ->
        when (c) {
            in 'a'..'z' -> 'a' + (c - 'a' + 13) % 26
            in 'A'..'Z' -> 'A' + (c - 'A' + 13) % 26
            else -> c
        }
    }.joinToString("")

    private fun decodeHtml(input: String): String {
        var s = input
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&amp;", "&")
        s = Regex("&#(\\d+);").replace(s) { it.groupValues[1].toInt().toChar().toString() }
        s = Regex("&#x([0-9a-fA-F]+);").replace(s) { it.groupValues[1].toInt(16).toChar().toString() }
        return s
    }
}

/** Supported hashing algorithms. */
enum class HashAlgo(val label: String, val jca: String) {
    MD5("MD5", "MD5"),
    SHA1("SHA-1", "SHA-1"),
    SHA256("SHA-256", "SHA-256"),
    SHA512("SHA-512", "SHA-512")
}

object Hashing {
    fun hash(input: String, algo: HashAlgo): String {
        val digest = MessageDigest.getInstance(algo.jca)
        val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun all(input: String): List<Pair<HashAlgo, String>> =
        HashAlgo.entries.map { it to hash(input, it) }
}

/**
 * Offline string-manipulation helpers used by the String Lab screen
 * (classic HackBar "OTHER" tools). All pure, no I/O.
 */
object StringOps {

    /** Escape quotes & backslashes (PHP addslashes equivalent). */
    fun addslashes(input: String): String = buildString {
        input.forEach { c ->
            when (c) {
                '\'', '"', '\\' -> { append('\\'); append(c) }
                '\u0000' -> append("\\0")
                else -> append(c)
            }
        }
    }

    /** Remove escaping backslashes (PHP stripslashes equivalent). */
    fun stripslashes(input: String): String = buildString {
        var i = 0
        while (i < input.length) {
            val c = input[i]
            if (c == '\\' && i + 1 < input.length) {
                append(input[i + 1]); i += 2
            } else { append(c); i++ }
        }
    }

    fun stripSpaces(input: String): String = input.replace(Regex("\\s+"), "")

    fun stripCustom(input: String, chars: String): String =
        if (chars.isEmpty()) input else input.filterNot { it in chars }

    fun reverse(input: String): String = input.reversed()

    /** Alternating-case string e.g. "hello" -> "HeLlO". */
    fun randomCase(input: String): String {
        var upper = true
        return buildString {
            input.forEach { c ->
                if (c.isLetter()) {
                    append(if (upper) c.uppercaseChar() else c.lowercaseChar())
                    upper = !upper
                } else append(c)
            }
        }
    }

    /** Buffer-overflow test pattern: [count] repeated chars. */
    fun bufferPattern(count: Int, ch: Char = 'A'): String =
        if (count <= 0) "" else ch.toString().repeat(count)

    /** Decimal integer -> hex string (e.g. 255 -> "ff"). null if not an int. */
    fun intToHex(input: String): String? =
        input.trim().toLongOrNull()?.toString(16)

    /** Hex string -> decimal integer (accepts 0x / : / spaces). null if invalid. */
    fun hexToInt(input: String): String? {
        val clean = input.trim().lowercase().removePrefix("0x").replace(Regex("[\\s:]"), "")
        return clean.toLongOrNull(16)?.toString()
    }

    /** Named ready-to-paste reference strings. */
    val usefulStrings: List<Pair<String, String>> = listOf(
        "PI (50)" to "3.14159265358979323846264338327950288419716939937510",
        "PHI (golden)" to "1.61803398874989484820458683436563811772030917980576",
        "Fibonacci" to "0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597",
        "Lorem Ipsum" to "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        "Alphabet" to "abcdefghijklmnopqrstuvwxyz",
        "ASCII printable" to "!\"#\$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"
    )
}
