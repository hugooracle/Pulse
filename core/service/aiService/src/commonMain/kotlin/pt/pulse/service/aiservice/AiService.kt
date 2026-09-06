package pt.pulse.service.aiservice

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import pt.pulse.core.domain.data.model.metadata.Line
import pt.pulse.core.domain.data.model.metadata.Lyrics
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

class AiService(
    private val aiHost: AIHost = AIHost.GEMINI,
    private val apiKey: String,
    private val customModelId: String? = null,
    private val customBaseUrl: String? = null,
    private val customHeaders: Map<String, String>? = null,
) {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        }

    private val httpClient =
        HttpClient {
            expectSuccess = false
        }

    private val model: String
        get() =
            customModelId?.takeIf { it.isNotBlank() }
                ?: when (aiHost) {
                    AIHost.GEMINI -> "gemini-2.0-flash"
                    AIHost.OPENAI -> "gpt-4o"
                    AIHost.CUSTOM_OPENAI -> "gpt-4o"
                }

    private val chatCompletionsUrl: String
        get() {
            val baseUrl =
                when (aiHost) {
                    AIHost.GEMINI -> "https://generativelanguage.googleapis.com/v1beta/openai"
                    AIHost.OPENAI -> "https://api.openai.com/v1"
                    AIHost.CUSTOM_OPENAI -> customBaseUrl ?: "https://api.openai.com/v1"
                }.trimEnd('/')

            return if (baseUrl.endsWith("/chat/completions")) {
                baseUrl
            } else {
                "$baseUrl/chat/completions"
            }
        }

    suspend fun translateLyrics(
        inputLyrics: Lyrics,
        targetLanguage: String,
    ): Lyrics {
        val lines = inputLyrics.lines ?: throw IllegalStateException("No lyrics lines to translate")

        val indexToWords = mutableMapOf<String, String>()
        lines.forEachIndexed { index, line ->
            val words = line.words.trim()
            if (words.isNotEmpty() && words != "♫") {
                indexToWords[index.toString()] = words
            }
        }

        if (indexToWords.isEmpty()) {
            throw IllegalStateException("No translatable lyrics lines found")
        }

        val inputJson = json.encodeToString(MapSerializer(String.serializer(), String.serializer()), indexToWords)
        val systemPrompt =
            "You are a song lyrics translation assistant.\n\n" +
                "TASK:\n" +
                "- You will receive a JSON object where keys are line indices and values are lyrics text.\n" +
                "- FIRST, detect the dominant language of the input lyrics.\n" +
                "- If the detected language is the SAME as the target language code, return an EMPTY \"translations\" object. Do NOT translate. Do NOT paraphrase.\n" +
                "- Otherwise, translate ONLY the values to the target language.\n" +
                "- Keep ALL keys exactly the same, do not merge, split, add or remove entries, and preserve the song's meaning, tone and emotion.\n\n" +
                "OUTPUT:\n" +
                "- Return only valid JSON in this format: {\"translations\": {\"0\": \"translated text\"}}."

        val requestBody =
            buildJsonObject {
                put("model", model)
                putJsonArray("messages") {
                    add(
                        buildJsonObject {
                            put("role", "system")
                            put("content", systemPrompt)
                        },
                    )
                    add(
                        buildJsonObject {
                            put("role", "user")
                            put("content", "Target language: $targetLanguage\nInput lyrics: $inputJson")
                        },
                    )
                }
            }

        val response =
            httpClient.post(chatCompletionsUrl) {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                customHeaders?.let { values ->
                    headers {
                        values.forEach { (name, value) -> append(name, value) }
                    }
                }
                setBody(requestBody.toString())
            }

        val responseText = response.bodyAsText()
        val responseJson =
            runCatching { json.parseToJsonElement(responseText).jsonObject }
                .getOrElse {
                    throw IllegalStateException("Invalid AI response (${response.status.value})")
                }

        responseJson["error"]?.jsonObject?.get("message")?.jsonPrimitive?.contentOrNull?.let { message ->
            throw IllegalStateException(message)
        }

        if (response.status.value !in 200..299) {
            throw IllegalStateException("AI request failed with HTTP ${response.status.value}")
        }

        val jsonContent =
            responseJson["choices"]
                ?.jsonArray
                ?.firstOrNull()
                ?.jsonObject
                ?.get("message")
                ?.jsonObject
                ?.get("content")
                ?.jsonPrimitive
                ?.contentOrNull
                ?: throw IllegalStateException("No response from AI")

        val cleanedJson =
            Regex("```json\\s*([\\s\\S]*?)```")
                .find(jsonContent)
                ?.groupValues
                ?.getOrNull(1)
                ?: jsonContent.replace("```json", "").replace("```", "")

        val translationResponse = json.decodeFromString<TranslationResponse>(cleanedJson.trim())
        val translatedMap = translationResponse.translations
        if (translatedMap.isEmpty()) {
            throw IllegalStateException(
                "Input lyrics are already in the target language ($targetLanguage). Translation aborted.",
            )
        }

        val translatedLines =
            lines.mapIndexed { index, originalLine ->
                val translatedWords = translatedMap[index.toString()]
                if (translatedWords != null) {
                    Line(
                        startTimeMs = originalLine.startTimeMs,
                        endTimeMs = originalLine.endTimeMs,
                        words = translatedWords,
                        syllables = null,
                    )
                } else {
                    Line(
                        startTimeMs = originalLine.startTimeMs,
                        endTimeMs = originalLine.endTimeMs,
                        words = originalLine.words,
                        syllables = originalLine.syllables,
                    )
                }
            }

        return Lyrics(
            error = false,
            lines = translatedLines,
            syncType = inputLyrics.syncType,
        )
    }
}

@kotlinx.serialization.Serializable
data class TranslationResponse(
    val translations: Map<String, String> = emptyMap(),
)

enum class AIHost {
    GEMINI,
    OPENAI,
    CUSTOM_OPENAI,
}
