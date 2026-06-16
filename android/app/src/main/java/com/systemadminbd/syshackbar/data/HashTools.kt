package com.systemadminbd.syshackbar.data

import java.security.MessageDigest

/**
 * Offline hash identification + dictionary cracking.
 * No external service required — works fully offline for educational use.
 */
object HashTools {

    /** Guess the algorithm family from a hash's length / charset. */
    fun identify(raw: String): List<String> {
        val h = raw.trim()
        if (h.isEmpty()) return emptyList()
        val isHex = h.matches(Regex("^[0-9a-fA-F]+$"))
        return when {
            !isHex && h.startsWith("\$2") -> listOf("bcrypt")
            !isHex && h.startsWith("\$1\$") -> listOf("MD5 crypt (Unix)")
            !isHex && h.startsWith("\$6\$") -> listOf("SHA-512 crypt (Unix)")
            !isHex && h.startsWith("\$5\$") -> listOf("SHA-256 crypt (Unix)")
            isHex && h.length == 32 -> listOf("MD5", "NTLM", "MD4")
            isHex && h.length == 40 -> listOf("SHA-1")
            isHex && h.length == 56 -> listOf("SHA-224")
            isHex && h.length == 64 -> listOf("SHA-256")
            isHex && h.length == 96 -> listOf("SHA-384")
            isHex && h.length == 128 -> listOf("SHA-512")
            else -> listOf("Unknown / unsupported")
        }
    }

    private fun digest(input: String, jca: String): String {
        val md = MessageDigest.getInstance(jca)
        return md.digest(input.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
    }

    /**
     * Try to crack a hex hash against a built-in common-password wordlist.
     * Supports MD5 / SHA-1 / SHA-256 / SHA-512, plus NTLM and MD4 (both 32 hex
     * chars, same length as MD5). Returns the plaintext or null.
     */
    fun crack(raw: String): String? {
        val target = raw.trim().lowercase()
        // Candidate hash functions to try for this length. For 32-char hashes
        // we attempt MD5, MD4 and NTLM since identify() lists all three.
        val hashers: List<(String) -> String> = when (target.length) {
            32 -> listOf(
                { w -> digest(w, "MD5") },
                { w -> md4Hex(w.toByteArray(Charsets.UTF_8)) },
                { w -> md4Hex(w.toByteArray(Charsets.UTF_16LE)) } // NTLM
            )
            40 -> listOf { w -> digest(w, "SHA-1") }
            64 -> listOf { w -> digest(w, "SHA-256") }
            128 -> listOf { w -> digest(w, "SHA-512") }
            else -> return null
        }
        for (word in WORDLIST) {
            val variants = listOf(word, word.replaceFirstChar { it.uppercase() })
            for (variant in variants) {
                for (hasher in hashers) {
                    if (hasher(variant) == target) return variant
                }
            }
        }
        return null
    }

    /** Compact built-in wordlist of the most common passwords for demo cracking. */
    private val WORDLIST: List<String> = listOf(
        "123456", "password", "123456789", "12345678", "12345", "qwerty",
        "abc123", "111111", "1234567", "sunshine", "iloveyou", "admin",
        "welcome", "monkey", "login", "princess", "dragon", "passw0rd",
        "master", "hello", "letmein", "football", "secret", "test",
        "root", "toor", "pass", "1234", "0000", "1q2w3e4r",
        "qwertyuiop", "superman", "batman", "trustno1", "whatever",
        "shadow", "ashley", "michael", "computer", "ninja", "starwars",
        "hacker", "linux", "android", "google", "facebook", "syshackbar",
        "darknet", "haxor", "kali", "metasploit", "nmap", "anonymous"
    )
}

/**
 * Pure-Kotlin MD4 (RFC 1320). Android's [MessageDigest] does not ship MD4, which
 * NTLM (MD4 over UTF-16LE) depends on, so it is implemented here. Returns the
 * lowercase hex digest.
 */
private fun md4Hex(message: ByteArray): String {
    var a = 0x67452301
    var b = 0xefcdab89.toInt()
    var c = 0x98badcfe.toInt()
    var d = 0x10325476

    // Pad: 0x80, then zeros until length ≡ 56 (mod 64), then 64-bit little-endian bit length.
    val bitLen = message.size.toLong() * 8
    val padLen = ((56 - (message.size + 1) % 64) + 64) % 64
    val padded = ByteArray(message.size + 1 + padLen + 8)
    message.copyInto(padded)
    padded[message.size] = 0x80.toByte()
    for (i in 0 until 8) {
        padded[padded.size - 8 + i] = ((bitLen ushr (8 * i)) and 0xff).toByte()
    }

    fun rotl(x: Int, s: Int): Int = (x shl s) or (x ushr (32 - s))
    val x = IntArray(16)
    var off = 0
    while (off < padded.size) {
        for (i in 0 until 16) {
            val j = off + i * 4
            x[i] = (padded[j].toInt() and 0xff) or
                ((padded[j + 1].toInt() and 0xff) shl 8) or
                ((padded[j + 2].toInt() and 0xff) shl 16) or
                ((padded[j + 3].toInt() and 0xff) shl 24)
        }
        val aa = a; val bb = b; val cc = c; val dd = d

        // Round 1: F(x,y,z) = (x & y) | (~x & z)
        val r1 = intArrayOf(3, 7, 11, 19)
        for (i in 0 until 16) {
            val f = (b and c) or (b.inv() and d)
            val t = a + f + x[i]
            a = rotl(t, r1[i % 4])
            val tmp = a; a = d; d = c; c = b; b = tmp
        }
        // Round 2: G(x,y,z) = (x & y) | (x & z) | (y & z), + 0x5a827999
        val r2 = intArrayOf(3, 5, 9, 13)
        val o2 = intArrayOf(0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15)
        for (i in 0 until 16) {
            val g = (b and c) or (b and d) or (c and d)
            val t = a + g + x[o2[i]] + 0x5a827999
            a = rotl(t, r2[i % 4])
            val tmp = a; a = d; d = c; c = b; b = tmp
        }
        // Round 3: H(x,y,z) = x ^ y ^ z, + 0x6ed9eba1
        val r3 = intArrayOf(3, 9, 11, 15)
        val o3 = intArrayOf(0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15)
        for (i in 0 until 16) {
            val h = b xor c xor d
            val t = a + h + x[o3[i]] + 0x6ed9eba1
            a = rotl(t, r3[i % 4])
            val tmp = a; a = d; d = c; c = b; b = tmp
        }

        a += aa; b += bb; c += cc; d += dd
        off += 64
    }

    val out = StringBuilder(32)
    for (word in intArrayOf(a, b, c, d)) {
        for (i in 0 until 4) {
            out.append("%02x".format((word ushr (8 * i)) and 0xff))
        }
    }
    return out.toString()
}
