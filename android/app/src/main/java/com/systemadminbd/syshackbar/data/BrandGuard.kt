package com.systemadminbd.syshackbar.data

import android.content.Context
import com.systemadminbd.syshackbar.BuildConfig
import com.systemadminbd.syshackbar.R
import java.security.MessageDigest

/**
 * Runtime branding integrity guard for SysHackBar / SystemAdminBD.
 *
 * SysHackBar is open source, but the SystemAdminBD identity (application id,
 * app label, credit line and logo) is protected. This guard re-verifies that
 * identity every launch. If any protected value has been altered, the app
 * throws on startup and refuses to run — complementing the build-time
 * `verifyBranding` Gradle task so tampering breaks both the build and runtime.
 */
object BrandGuard {

    const val APP_ID: String = "com.systemadminbd.syshackbar"
    const val APP_LABEL: String = "SysHackBar"
    const val CREDIT: String = "coded by systemadminbd"
    const val OWNER: String = "systemadminbd"

    /**
     * Fingerprint of the protected branding constants. Computed from the values
     * above; any edit to APP_ID, APP_LABEL, CREDIT or OWNER changes this digest
     * and trips the guard.
     */
    private const val EXPECTED_FINGERPRINT: String =
        "6d98b49bb165c2db0ead230eb8ca0f56c580fa61bf88d3ee25649977d0ebe396"

    /**
     * Verifies the protected identity. Throws [IllegalStateException] if the
     * branding has been tampered with. Call once from the launcher activity.
     */
    fun verify(context: Context) {
        // 1. Application id must be intact.
        check(BuildConfig.APPLICATION_ID == APP_ID) {
            "Branding integrity violation: application id changed."
        }
        // 2. Package the app actually runs under must match.
        check(context.packageName == APP_ID) {
            "Branding integrity violation: package mismatch."
        }
        // 3. The visible app label must remain SysHackBar.
        val label = context.applicationInfo.loadLabel(context.packageManager).toString()
        check(label == APP_LABEL) {
            "Branding integrity violation: app label changed."
        }
        // 4. The protected credit / owner constants must not be altered.
        check(CREDIT == "coded by " + OWNER) {
            "Branding integrity violation: credit line changed."
        }
        check(OWNER == "systemadminbd") {
            "Branding integrity violation: owner changed."
        }
        // 5. The bundled logo resource must still be present.
        check(R.drawable.brand_logo != 0) {
            "Branding integrity violation: logo removed."
        }
        // 6. Fingerprint of the protected constants must match the embedded one.
        check(fingerprint() == EXPECTED_FINGERPRINT) {
            "Branding integrity violation: branding fingerprint mismatch."
        }
    }

    private fun fingerprint(): String {
        val payload = "$APP_ID|$APP_LABEL|$CREDIT|$OWNER"
        return MessageDigest.getInstance("SHA-256")
            .digest(payload.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }
}
