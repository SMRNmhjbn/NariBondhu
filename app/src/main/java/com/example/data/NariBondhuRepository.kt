package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

class NariBondhuRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth,
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String =
        auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")

    private fun <T> wrapFirestoreStream(
        uid: String,
        path: String,
        upstream: Flow<T>,
    ): Flow<T> = upstream.catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, OperationType.LIST, path)
        }
        throw (firestoreError ?: error)
    }

    fun observeProfile(userId: String): Flow<UserProfile?> = flow {
        if (userId.isBlank()) {
            emit(null)
            return@flow
        }
        val path = "users/$userId"
        emitAll(
            wrapFirestoreStream(
                uid = userId,
                path = path,
                upstream = db.collection("users").document(userId)
                    .snapshots()
                    .map { snapshot ->
                        if (snapshot.exists()) {
                            snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        } else {
                            null
                        }
                    }
            )
        )
    }

    fun observePeriods(userId: String): Flow<List<MenstrualPeriod>> = flow {
        if (userId.isBlank()) {
            emit(emptyList())
            return@flow
        }
        val path = "users/$userId/menstrual_periods"
        emitAll(
            wrapFirestoreStream(
                uid = userId,
                path = path,
                upstream = db.collection("users").document(userId)
                    .collection("menstrual_periods")
                    .orderBy("startDateEpochDay", Query.Direction.DESCENDING)
                    .snapshots()
                    .map { snap -> snap.toObjects(MenstrualPeriod::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            )
        )
    }

    fun observeSymptoms(userId: String): Flow<List<SymptomEntry>> = flow {
        if (userId.isBlank()) {
            emit(emptyList())
            return@flow
        }
        val path = "users/$userId/symptoms"
        emitAll(
            wrapFirestoreStream(
                uid = userId,
                path = path,
                upstream = db.collection("users").document(userId)
                    .collection("symptoms")
                    .orderBy("dateEpochDay", Query.Direction.DESCENDING)
                    .snapshots()
                    .map { snap -> snap.toObjects(SymptomEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            )
        )
    }

    fun observeWellnessLogs(userId: String): Flow<List<WellnessLog>> = flow {
        if (userId.isBlank()) {
            emit(emptyList())
            return@flow
        }
        val path = "users/$userId/wellness_logs"
        emitAll(
            wrapFirestoreStream(
                uid = userId,
                path = path,
                upstream = db.collection("users").document(userId)
                    .collection("wellness_logs")
                    .orderBy("dateEpochDay", Query.Direction.DESCENDING)
                    .snapshots()
                    .map { snap -> snap.toObjects(WellnessLog::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            )
        )
    }

    fun observeHealthRecords(userId: String): Flow<List<HealthRecord>> = flow {
        if (userId.isBlank()) {
            emit(emptyList())
            return@flow
        }
        val path = "users/$userId/health_records"
        emitAll(
            wrapFirestoreStream(
                uid = userId,
                path = path,
                upstream = db.collection("users").document(userId)
                    .collection("health_records")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .snapshots()
                    .map { snap -> snap.toObjects(HealthRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            )
        )
    }

    fun observeBookings(userId: String): Flow<List<AppointmentBooking>> = flow {
        if (userId.isBlank()) {
            emit(emptyList())
            return@flow
        }
        val path = "users/$userId/bookings"
        emitAll(
            wrapFirestoreStream(
                uid = userId,
                path = path,
                upstream = db.collection("users").document(userId)
                    .collection("bookings")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .snapshots()
                    .map { snap -> snap.toObjects(AppointmentBooking::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            )
        )
    }

    fun observeConsentLogs(userId: String): Flow<List<ConsentLog>> = flow {
        if (userId.isBlank()) {
            emit(emptyList())
            return@flow
        }
        val path = "users/$userId/consent_logs"
        emitAll(
            wrapFirestoreStream(
                uid = userId,
                path = path,
                upstream = db.collection("users").document(userId)
                    .collection("consent_logs")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .snapshots()
                    .map { snap -> snap.toObjects(ConsentLog::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) }
            )
        )
    }

    suspend fun saveInitialProfile(profile: UserProfile): Result<Unit> {
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
            path = docRef.path
            val existing = docRef.get().await()
            if (existing.exists()) {
                docRef.update(
                    mapOf(
                        "displayName" to profile.displayName.trim().take(80).ifEmpty { "User" },
                        "preferredLanguage" to if (profile.preferredLanguage == "BN") "BN" else "EN",
                        "city" to profile.city.trim().take(50),
                        "ageConfirmed18Plus" to profile.ageConfirmed18Plus,
                        "healthInterests" to profile.healthInterests.take(10),
                        "discreetMode" to profile.discreetMode,
                        "onboardingCompleted" to profile.onboardingCompleted,
                        "remindersEnabled" to profile.remindersEnabled,
                        "aiConsentGranted" to profile.aiConsentGranted,
                        "cloudSyncConsentGranted" to profile.cloudSyncConsentGranted,
                        "updatedAt" to FieldValue.serverTimestamp(),
                    )
                ).await()
            } else {
                docRef.set(profile.copy(userId = uid).toCreateMap()).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun updateProfileFields(fields: Map<String, Any>): Result<Unit> {
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
            path = docRef.path
            val payload = fields.toMutableMap().apply {
                put("updatedAt", FieldValue.serverTimestamp())
            }
            docRef.update(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun logConsent(consentType: ConsentType, granted: Boolean, summary: String): Result<Unit> {
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}/consent_logs"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("consent_logs").document()
            path = docRef.path
            val log = ConsentLog(
                userId = uid,
                consentType = consentType.name,
                granted = granted,
                summary = summary
            )
            docRef.set(log.toCreateMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun savePeriod(period: MenstrualPeriod): Result<Unit> {
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}/menstrual_periods"
        return try {
            val uid = requireUserId()
            val colRef = db.collection("users").document(uid).collection("menstrual_periods")
            if (period.id.isBlank()) {
                val docRef = colRef.document()
                path = docRef.path
                docRef.set(period.copy(userId = uid).toCreateMap()).await()
            } else {
                val docRef = colRef.document(period.id)
                path = docRef.path
                val updates = mutableMapOf<String, Any>(
                    "startDateEpochDay" to period.startDateEpochDay.coerceIn(0L, 100000L),
                    "flowIntensity" to FlowIntensity.fromName(period.flowIntensity).name,
                    "spottingOnly" to period.spottingOnly,
                    "notes" to period.notes.trim().take(500),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
                if (period.endDateEpochDay != null) {
                    updates["endDateEpochDay"] = period.endDateEpochDay.coerceIn(period.startDateEpochDay, 100000L)
                } else {
                    updates["endDateEpochDay"] = FieldValue.delete()
                }
                docRef.update(updates).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun deletePeriod(periodId: String): Result<Unit> = deleteSubdoc("menstrual_periods", periodId)

    suspend fun addSymptom(entry: SymptomEntry): Result<Unit> {
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}/symptoms"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("symptoms").document()
            path = docRef.path
            docRef.set(entry.copy(userId = uid).toCreateMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteSymptom(symptomId: String): Result<Unit> = deleteSubdoc("symptoms", symptomId)

    suspend fun addWellnessLog(log: WellnessLog): Result<Unit> {
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}/wellness_logs"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("wellness_logs").document()
            path = docRef.path
            docRef.set(log.copy(userId = uid).toCreateMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun addHealthRecord(record: HealthRecord): Result<Unit> {
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}/health_records"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("health_records").document()
            path = docRef.path
            docRef.set(record.copy(userId = uid).toCreateMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateHealthRecordSharing(recordId: String, sharedWithDoctorIds: List<String>): Result<Unit> {
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}/health_records/$recordId"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("health_records").document(recordId)
            path = docRef.path
            docRef.update(
                mapOf(
                    "sharedWithDoctorIds" to sharedWithDoctorIds.take(20),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteHealthRecord(recordId: String): Result<Unit> = deleteSubdoc("health_records", recordId)

    suspend fun createBooking(booking: AppointmentBooking): Result<Unit> {
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}/bookings"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("bookings").document()
            path = docRef.path
            docRef.set(booking.copy(userId = uid).toCreateMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateBookingStatus(
        bookingId: String,
        newStatus: BookingStatus,
        newSlotLabel: String,
    ): Result<Unit> {
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}/bookings/$bookingId"
        return try {
            val uid = requireUserId()
            require(bookingId.isNotBlank()) { "Booking ID required" }
            val docRef = db.collection("users").document(uid).collection("bookings").document(bookingId)
            path = docRef.path
            docRef.update(
                mapOf(
                    "status" to newStatus.name,
                    "slotLabel" to newSlotLabel.trim().take(80),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    private suspend fun deleteSubdoc(collectionName: String, docId: String): Result<Unit> {
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}/$collectionName/$docId"
        return try {
            val uid = requireUserId()
            require(docId.isNotBlank()) { "Document ID required" }
            val docRef = db.collection("users").document(uid).collection(collectionName).document(docId)
            path = docRef.path
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    fun buildExportJson(
        profile: UserProfile?,
        periods: List<MenstrualPeriod>,
        symptoms: List<SymptomEntry>,
        wellnessLogs: List<WellnessLog>,
        records: List<HealthRecord>,
        bookings: List<AppointmentBooking>,
    ): String {
        val root = JSONObject()
        root.put("platform", "NariBondhu")
        root.put("displayName", profile?.displayName ?: "")
        root.put("city", profile?.city ?: "")
        root.put("preferredLanguage", profile?.preferredLanguage ?: "EN")
        root.put("periods", JSONArray().apply {
            periods.forEach { p ->
                put(JSONObject().apply {
                    put("startEpochDay", p.startDateEpochDay)
                    put("endEpochDay", p.endDateEpochDay ?: JSONObject.NULL)
                    put("flowIntensity", p.flowIntensity)
                    put("spottingOnly", p.spottingOnly)
                    put("notes", p.notes)
                })
            }
        })
        root.put("symptoms", JSONArray().apply {
            symptoms.forEach { s ->
                put(JSONObject().apply {
                    put("dateEpochDay", s.dateEpochDay)
                    put("category", s.category)
                    put("symptomType", s.symptomType)
                    put("severity", s.severity)
                    put("notes", s.notes)
                })
            }
        })
        root.put("wellnessLogs", JSONArray().apply {
            wellnessLogs.forEach { w ->
                put(JSONObject().apply {
                    put("dateEpochDay", w.dateEpochDay)
                    put("sleepHours", w.sleepHours)
                    put("mood", w.mood)
                    put("stressLevel", w.stressLevel)
                    put("energyLevel", w.energyLevel)
                    put("hydrationGlasses", w.hydrationGlasses)
                })
            }
        })
        root.put("healthRecords", JSONArray().apply {
            records.forEach { r ->
                put(JSONObject().apply {
                    put("recordType", r.recordType)
                    put("title", r.title)
                    put("dateText", r.dateText)
                    put("details", r.details)
                })
            }
        })
        root.put("bookings", JSONArray().apply {
            bookings.forEach { b ->
                put(JSONObject().apply {
                    put("doctorName", b.doctorName)
                    put("clinicName", b.clinicName)
                    put("slotLabel", b.slotLabel)
                    put("status", b.status)
                })
            }
        })
        return root.toString(2)
    }

    suspend fun deleteAllUserData(): Result<Unit> {
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
        return try {
            val uid = requireUserId()
            val userDoc = db.collection("users").document(uid)
            val subcollections = listOf(
                "menstrual_periods",
                "symptoms",
                "wellness_logs",
                "health_records",
                "bookings",
                "consent_logs",
            )
            for (sub in subcollections) {
                path = "users/$uid/$sub"
                val docs = userDoc.collection(sub).get().await()
                for (doc in docs.documents) {
                    doc.reference.delete().await()
                }
            }
            path = userDoc.path
            userDoc.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }
}
