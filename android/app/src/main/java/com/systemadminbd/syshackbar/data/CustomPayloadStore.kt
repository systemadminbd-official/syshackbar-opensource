package com.systemadminbd.syshackbar.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/** A user-saved custom payload. */
data class CustomPayload(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val value: String
)

/** Persists user payloads to SharedPreferences as JSON. */
class CustomPayloadViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("dh_custom_payloads", Application.MODE_PRIVATE)

    private val _payloads = MutableStateFlow<List<CustomPayload>>(emptyList())
    val payloads: StateFlow<List<CustomPayload>> = _payloads.asStateFlow()

    init {
        _payloads.value = load()
    }

    fun add(title: String, value: String) {
        _payloads.value = listOf(CustomPayload(title = title, value = value)) + _payloads.value
        persist()
    }

    fun update(id: String, title: String, value: String) {
        _payloads.value = _payloads.value.map {
            if (it.id == id) it.copy(title = title, value = value) else it
        }
        persist()
    }

    fun remove(id: String) {
        _payloads.value = _payloads.value.filterNot { it.id == id }
        persist()
    }

    private fun persist() {
        val arr = JSONArray()
        _payloads.value.forEach {
            arr.put(JSONObject().apply {
                put("id", it.id)
                put("title", it.title)
                put("value", it.value)
            })
        }
        prefs.edit().putString("items", arr.toString()).apply()
    }

    private fun load(): List<CustomPayload> {
        val raw = prefs.getString("items", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                CustomPayload(o.getString("id"), o.getString("title"), o.getString("value"))
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
