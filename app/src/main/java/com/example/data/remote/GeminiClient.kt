package com.example.data.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateContent(
        prompt: String,
        systemInstructionText: String? = "أنت مساعد أكاديمي متخصص في البحث العلمي للمرحلة الجامعية والدراسات العليا. تقدم إجابات موثوقة باللغة العربية الفصحى الأكاديمية مع الالتزام التام بأساليب البحث العلمي وتوجيه الباحث للتحليل النقدي والتفكير المستقل، والالتزام بمعايير النزاهة الأكاديمية."
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.failure(
                    IllegalStateException("مفتاح GEMINI_API_KEY غير متاح في إعدادات التطبيق. يرجى التوجه لضبطه في Secrets Panel.")
                )
            }

            // Build JSON request manually using Android's native org.json
            val rootJson = JSONObject()

            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", prompt)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            rootJson.put("contents", contentsArray)

            val genConfigObj = JSONObject()
            genConfigObj.put("temperature", 0.4)
            genConfigObj.put("topP", 0.95)
            rootJson.put("generationConfig", genConfigObj)

            if (!systemInstructionText.isNull_or_empty()) {
                val sysInstObj = JSONObject()
                val sysPartsArray = JSONArray()
                val sysPartObj = JSONObject()
                sysPartObj.put("text", systemInstructionText)
                sysPartsArray.put(sysPartObj)
                sysInstObj.put("parts", sysPartsArray)
                rootJson.put("systemInstruction", sysInstObj)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = rootJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(body)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseString = response.body?.string()

            if (!response.isSuccessful || responseString == null) {
                return@withContext Result.failure(
                    Exception("خطأ في الاتصال بالخدمة (${response.code})")
                )
            }

            val responseJson = JSONObject(responseString)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val contentRes = firstCandidate?.optJSONObject("content")
            val partsRes = contentRes?.optJSONArray("parts")
            val firstPart = partsRes?.optJSONObject(0)
            val outputText = firstPart?.optString("text")

            if (!outputText.isNullOrEmpty()) {
                Result.success(outputText)
            } else {
                Result.failure(Exception("لم يتم الحصول على نتيجة نصية."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()
}
