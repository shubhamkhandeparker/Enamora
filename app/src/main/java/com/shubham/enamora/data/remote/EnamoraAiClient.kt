package com.shubham.enamora.data.remote

import com.shubham.enamora.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

data class AiRecentMessage(
    val role: String,
    val text: String
)

class EnamoraAiClient(
    private val httpClient: OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(
                20,
                TimeUnit.SECONDS
            )
            .readTimeout(
                90,
                TimeUnit.SECONDS
            )
            .writeTimeout(
                20,
                TimeUnit.SECONDS
            )
            .build()
) {
    companion object {
        private val jsonMediaType =
            "application/json; charset=utf-8"
                .toMediaType()
    }

    suspend fun generateReply(
        characterId: String,
        systemPrompt: String,
        recentMessages:
        List<AiRecentMessage>,
        userMessage: String
    ): String {
        return withContext(
            Dispatchers.IO
        ) {
            val baseUrl =
                BuildConfig
                    .ENAMORA_API_BASE_URL
                    .trim()
                    .trimEnd('/')

            val developmentKey =
                BuildConfig
                    .ENAMORA_DEV_KEY
                    .trim()

            check(baseUrl.isNotEmpty()) {
                "Enamora API URL is missing."
            }

            check(
                developmentKey.isNotEmpty()
            ) {
                "Enamora development key is missing."
            }

            val recentMessagesJson =
                JSONArray().apply {
                    recentMessages.forEach {
                            message ->
                        put(
                            JSONObject().apply {
                                put(
                                    "role",
                                    message.role
                                )
                                put(
                                    "text",
                                    message.text
                                )
                            }
                        )
                    }
                }

            val requestJson =
                JSONObject().apply {
                    put(
                        "characterId",
                        characterId
                    )
                    put(
                        "systemPrompt",
                        systemPrompt
                    )
                    put(
                        "recentMessages",
                        recentMessagesJson
                    )
                    put(
                        "userMessage",
                        userMessage
                    )
                }

            val request =
                Request.Builder()
                    .url(
                        "$baseUrl/v1/chat"
                    )
                    .header(
                        "x-enamora-key",
                        developmentKey
                    )
                    .post(
                        requestJson
                            .toString()
                            .toRequestBody(
                                jsonMediaType
                            )
                    )
                    .build()

            httpClient
                .newCall(request)
                .execute()
                .use { response ->
                    val responseText =
                        response.body
                            ?.string()
                            .orEmpty()

                    if (!response.isSuccessful) {
                        val serverMessage =
                            try {
                                JSONObject(
                                    responseText
                                ).optString(
                                    "error"
                                )
                            } catch (
                                exception: Exception
                            ) {
                                ""
                            }

                        throw IOException(
                            serverMessage.ifBlank {
                                "AI service unavailable " +
                                        "(${response.code})."
                            }
                        )
                    }

                    val reply =
                        try {
                            JSONObject(
                                responseText
                            ).optString(
                                "reply"
                            ).trim()
                        } catch (
                            exception: Exception
                        ) {
                            throw IOException(
                                "Invalid AI response.",
                                exception
                            )
                        }

                    if (reply.isEmpty()) {
                        throw IOException(
                            "AI returned an empty reply."
                        )
                    }

                    reply
                }
        }
    }
}