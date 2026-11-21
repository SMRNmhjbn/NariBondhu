package com.example.data

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "NariBondhuAiRepo"

class NariBondhuAiRepository {

    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    You are NariBondhu AI (নারীবন্ধু AI), the educational women's health information assistant for NariBondhu in Bangladesh.
                    
                    STRICT SAFETY & SCOPE RULES (PRD Section 10, 11 & BMDC Telemedicine Guidelines July 2020):
                    1. You provide ONLY general, medically reviewed women's health education (menstrual health, PCOS, PMS, intimate hygiene, reproductive health, menopause, preventive health) and app navigation help.
                    2. You are NOT an AI doctor. NEVER diagnose diseases, NEVER prescribe medication, NEVER recommend changing medication doses, NEVER guarantee ovulation or fertility, NEVER interpret personal lab/ultrasound results as a diagnosis, and NEVER reassure a user that potentially serious symptoms are harmless.
                    3. Use simple, warm, respectful, non-judgmental language suitable for users with varying literacy levels. Reply in the requested language (Bangla or English).
                    4. Prioritise clinician-approved sources: NariBondhu Clinical Advisory Content, WHO, RCOG, NICE, and DGHS Bangladesh. Cite relevant source names when answering educational questions.
                    5. Whenever a concern is persistent, uncertain, worsening, or asks for diagnosis/prescription, clearly explain why a qualified gynaecologist should evaluate it and recommend booking a verified doctor through BondhuHelp.
                    6. For urgent red-flag symptoms (very heavy bleeding with dizziness/fainting, severe pelvic/abdominal pain, pregnancy with bleeding/pain), give immediate urgent-care instructions (go to nearest hospital emergency or call 999) and keep the message brief so care is not delayed.
                    """.trimIndent()
                )
            }
        )
    }

    private val reportExtractSchema = Schema.obj(
        properties = mapOf(
            "title" to Schema.string(description = "Short heading or test name from the medical document"),
            "recordType" to Schema.enumeration(
                values = listOf("LAB_REPORT", "ULTRASOUND", "CONSULTATION", "MEDICATION", "DIAGNOSIS", "NOTE"),
                description = "Closest document category"
            ),
            "dateText" to Schema.string(description = "Document date if visible, else empty string"),
            "details" to Schema.string(description = "Extracted test headings, numerical values, and reference ranges in plain text WITHOUT making any medical diagnosis")
        )
    )

    private val reportExtractModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = reportExtractSchema
            },
            systemInstruction = content {
                text(
                    """
                    Extract document metadata (title, category, dateText, test names, numerical results, reference ranges) from the uploaded health document photo for the user to verify before saving.
                    STRICT RULE: Do NOT diagnose any condition or interpret whether the patient has a disease. Only transcribe visible structured details clearly.
                    """.trimIndent()
                )
            }
        )
    }

    private val summarySchema = Schema.obj(
        properties = mapOf(
            "chronologicalSummary" to Schema.string(description = "Clear chronological summary of recorded dates, cycle stats, symptoms, and medications for doctor visit preparation, with zero diagnosis claims"),
            "suggestedQuestionsForDoctor" to Schema.array(
                items = Schema.string(),
                description = "3 to 5 simple, practical questions the patient can ask their gynaecologist"
            )
        )
    )

    private val summaryModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = summarySchema
            }
        )
    }

    fun streamEducationalResponse(
        userMessage: String,
        language: AppLanguage,
        safetyAssessment: SafetyAssessment,
        optionalContextSummary: String?,
    ): Flow<String> = flow {
        val langInstruction = if (language == AppLanguage.BN) {
            "Respond in clear, simple, warm Bangla (বাংলা)."
        } else {
            "Respond in clear, simple, warm English."
        }
        val tierNote = when (safetyAssessment.tier) {
            SafetyTier.RED -> "URGENT RED ESCALATION TRIGGERED. Keep response short, urge immediate emergency care (999 or nearest hospital), and do not delay with long discussion."
            SafetyTier.AMBER -> "AMBER PATHWAY TRIGGERED. Avoid guessing any diagnosis, explain limitations clearly, and advise consulting a verified gynaecologist on BondhuHelp."
            SafetyTier.GREEN -> "GREEN EDUCATIONAL PATHWAY. Give a helpful, source-backed explanation with a brief reminder of educational scope."
        }
        val prompt = buildString {
            appendLine(langInstruction)
            appendLine("Safety Instruction: $tierNote")
            if (!optionalContextSummary.isNullOrBlank()) {
                appendLine("User-authorized de-identified summary context (do not diagnose): $optionalContextSummary")
            }
            appendLine("User question: $userMessage")
        }

        val accumulated = StringBuilder()
        withTimeout(60_000L) {
            chatModel.generateContentStream(prompt).collect { chunk ->
                val piece = chunk.text.orEmpty()
                if (piece.isNotEmpty()) {
                    accumulated.append(piece)
                    emit(accumulated.toString())
                }
            }
        }
        if (accumulated.isBlank()) {
            throw IllegalStateException("NariBondhu AI returned an empty response.")
        }
    }

    suspend fun extractHealthReportPreview(bitmap: Bitmap): ExtractedReportPreview {
        val response = withTimeout(60_000L) {
            reportExtractModel.generateContent(
                content {
                    image(bitmap)
                    text("Extract the document title, category, date, and visible test values/reference ranges so the user can review and verify before saving.")
                }
            )
        }
        val jsonStr = response.text ?: throw IllegalStateException("Empty extraction response.")
        val json = JSONObject(jsonStr)
        return ExtractedReportPreview(
            title = json.optString("title", "Medical Document").take(120),
            recordType = HealthRecordType.fromName(json.optString("recordType", "LAB_REPORT")),
            dateText = json.optString("dateText", "").take(40),
            details = json.optString("details", "").take(1400),
        )
    }

    suspend fun generateConsultationSummary(
        chronologicalFacts: String,
        language: AppLanguage,
    ): Pair<String, List<String>> {
        val lang = if (language == AppLanguage.BN) "Bangla (বাংলা)" else "English"
        val response = withTimeout(60_000L) {
            summaryModel.generateContent(
                """
                Organise the following user-recorded menstrual dates, cycle statistics, symptoms, and health notes into a respectful, easy-to-read pre-consultation summary in $lang for a gynaecologist appointment.
                STRICT RULE: Do NOT conclude any diagnosis or prescribe any treatment. State facts chronologically and suggest 3-5 helpful questions the patient can ask the doctor.
                
                Facts:
                $chronologicalFacts
                """.trimIndent()
            )
        }
        val jsonStr = response.text ?: throw IllegalStateException("Empty summary response.")
        val json = JSONObject(jsonStr)
        val summary = json.optString("chronologicalSummary", "")
        val questionsArr = json.optJSONArray("suggestedQuestionsForDoctor")
        val questions = buildList {
            if (questionsArr != null) {
                for (i in 0 until questionsArr.length()) {
                    add(questionsArr.optString(i))
                }
            }
        }
        return summary to questions
    }
}
