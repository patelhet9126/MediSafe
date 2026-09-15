package com.medisafe.app.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.medisafe.app.data.model.*
import com.medisafe.app.utils.SecurityUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "medisafe.db"
        const val DATABASE_VERSION = 1

        // Table Names
        const val TABLE_USERS = "users"
        const val TABLE_PROFILES = "profiles"
        const val TABLE_CONTACTS = "emergency_contacts"
        const val TABLE_CONDITIONS = "medical_conditions"
        const val TABLE_ALLERGIES = "allergies"
        const val TABLE_MEDICATIONS = "medications"
        const val TABLE_REPORTS = "medical_reports"
        const val TABLE_FAMILY = "family_members"
        const val TABLE_ACCESS_REQUESTS = "access_requests"
        const val TABLE_AUDIT_LOGS = "audit_logs"

        @Volatile
        private var instance: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Users Table
        db.execSQL("""
            CREATE TABLE $TABLE_USERS (
                id TEXT PRIMARY KEY,
                full_name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                mobile_number TEXT NOT NULL,
                password_hash TEXT NOT NULL,
                salt TEXT NOT NULL,
                created_at TEXT NOT NULL
            )
        """.trimIndent())

        // Profiles Table
        db.execSQL("""
            CREATE TABLE $TABLE_PROFILES (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                unique_profile_id TEXT UNIQUE NOT NULL,
                full_name TEXT NOT NULL,
                preferred_name TEXT,
                dob TEXT,
                age INTEGER,
                gender TEXT,
                blood_group TEXT,
                mobile_number TEXT,
                email TEXT,
                address TEXT,
                city TEXT,
                state TEXT,
                country TEXT,
                height_cm INTEGER,
                weight_kg INTEGER,
                profile_photo_url TEXT,
                profile_completion INTEGER,
                is_primary INTEGER DEFAULT 1,
                owner_type TEXT,
                relationship TEXT,
                organ_donor_status TEXT,
                emergency_pin TEXT,
                qr_secure_token TEXT UNIQUE,
                card_color TEXT DEFAULT 'classic-navy',
                show_photo INTEGER DEFAULT 1,
                show_blood_group INTEGER DEFAULT 1,
                show_critical_allergy INTEGER DEFAULT 1,
                show_emergency_contact INTEGER DEFAULT 1,
                enable_lanyard INTEGER DEFAULT 1,
                created_at TEXT
            )
        """.trimIndent())

        // Emergency Contacts
        db.execSQL("""
            CREATE TABLE $TABLE_CONTACTS (
                id TEXT PRIMARY KEY,
                profile_id TEXT NOT NULL,
                full_name TEXT NOT NULL,
                relationship TEXT NOT NULL,
                mobile_number TEXT NOT NULL,
                alternate_number TEXT,
                email TEXT,
                is_primary INTEGER DEFAULT 0,
                priority_tier TEXT DEFAULT 'Primary',
                show_on_qr INTEGER DEFAULT 1,
                FOREIGN KEY (profile_id) REFERENCES $TABLE_PROFILES (id) ON DELETE CASCADE
            )
        """.trimIndent())

        // Medical Conditions
        db.execSQL("""
            CREATE TABLE $TABLE_CONDITIONS (
                id TEXT PRIMARY KEY,
                profile_id TEXT NOT NULL,
                name TEXT NOT NULL,
                category TEXT,
                diagnosed_date TEXT,
                status TEXT,
                is_current INTEGER DEFAULT 1,
                is_emergency_relevant INTEGER DEFAULT 0,
                hospital TEXT,
                city TEXT,
                doctor TEXT,
                treatment TEXT,
                stents_count INTEGER DEFAULT 0,
                graft_count INTEGER DEFAULT 0,
                status_desc TEXT,
                symptoms_desc TEXT,
                notes TEXT,
                FOREIGN KEY (profile_id) REFERENCES $TABLE_PROFILES (id) ON DELETE CASCADE
            )
        """.trimIndent())

        // Allergies
        db.execSQL("""
            CREATE TABLE $TABLE_ALLERGIES (
                id TEXT PRIMARY KEY,
                profile_id TEXT NOT NULL,
                type TEXT,
                name TEXT NOT NULL,
                allergen TEXT,
                reaction TEXT NOT NULL,
                severity TEXT,
                since_when TEXT,
                current_treatment TEXT,
                is_critical INTEGER DEFAULT 0,
                status TEXT,
                notes TEXT,
                FOREIGN KEY (profile_id) REFERENCES $TABLE_PROFILES (id) ON DELETE CASCADE
            )
        """.trimIndent())

        // Medications
        db.execSQL("""
            CREATE TABLE $TABLE_MEDICATIONS (
                id TEXT PRIMARY KEY,
                profile_id TEXT NOT NULL,
                name TEXT NOT NULL,
                dosage TEXT NOT NULL,
                frequency TEXT NOT NULL,
                reason TEXT,
                start_date TEXT,
                end_date TEXT,
                prescribed_by TEXT,
                is_critical INTEGER DEFAULT 0,
                status TEXT,
                notes TEXT,
                FOREIGN KEY (profile_id) REFERENCES $TABLE_PROFILES (id) ON DELETE CASCADE
            )
        """.trimIndent())

        // Medical Reports
        db.execSQL("""
            CREATE TABLE $TABLE_REPORTS (
                id TEXT PRIMARY KEY,
                profile_id TEXT NOT NULL,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                upload_date TEXT,
                report_date TEXT,
                file_size TEXT,
                file_path TEXT,
                hospital TEXT,
                doctor TEXT,
                related_condition TEXT,
                status TEXT,
                verified_by_patient INTEGER DEFAULT 1,
                notes TEXT,
                FOREIGN KEY (profile_id) REFERENCES $TABLE_PROFILES (id) ON DELETE CASCADE
            )
        """.trimIndent())

        // Family Members Table
        db.execSQL("""
            CREATE TABLE $TABLE_FAMILY (
                id TEXT PRIMARY KEY,
                profile_id TEXT NOT NULL,
                full_name TEXT NOT NULL,
                relationship TEXT NOT NULL,
                age INTEGER,
                gender TEXT,
                blood_group TEXT,
                photo_url TEXT,
                emergency_card_status TEXT DEFAULT 'Active',
                profile_completion INTEGER DEFAULT 85,
                primary_condition TEXT,
                emergency_contact TEXT,
                allergies_count INTEGER DEFAULT 0,
                records_count INTEGER DEFAULT 0
            )
        """.trimIndent())

        // Access Requests
        db.execSQL("""
            CREATE TABLE $TABLE_ACCESS_REQUESTS (
                id TEXT PRIMARY KEY,
                profile_id TEXT NOT NULL,
                requester_name TEXT NOT NULL,
                requester_role TEXT NOT NULL,
                organization TEXT NOT NULL,
                reason TEXT NOT NULL,
                duration_minutes INTEGER DEFAULT 30,
                status TEXT DEFAULT 'pending',
                requested_at TEXT,
                expires_at TEXT,
                pin_required INTEGER DEFAULT 1,
                FOREIGN KEY (profile_id) REFERENCES $TABLE_PROFILES (id) ON DELETE CASCADE
            )
        """.trimIndent())

        // Audit Logs
        db.execSQL("""
            CREATE TABLE $TABLE_AUDIT_LOGS (
                id TEXT PRIMARY KEY,
                profile_id TEXT NOT NULL,
                action TEXT NOT NULL,
                requester_name TEXT NOT NULL,
                requester_role TEXT NOT NULL,
                organization TEXT NOT NULL,
                information_accessed TEXT NOT NULL,
                status TEXT DEFAULT 'Authorized',
                timestamp TEXT NOT NULL
            )
        """.trimIndent())

        // Seed initial mock data so app is instantly ready with the approved prototype data
        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Safe upgrade without destructive recreation
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    // ==========================================
    // INITIAL DEMO DATA SEEDER
    // ==========================================
    private fun seedInitialData(db: SQLiteDatabase) {
        val userId = "usr_aarav_patel_001"
        val profileId = "prof_aarav_001"
        val uniqueProfileId = "MED-8924-7193"
        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashPassword("SecurePass#2026", salt)

        // 1. Insert User
        db.execSQL(
            """INSERT INTO $TABLE_USERS (id, full_name, email, mobile_number, password_hash, salt, created_at)
               VALUES (?, ?, ?, ?, ?, ?, ?)""",
            arrayOf(userId, "Aarav Patel", "aarav.patel@example.com", "+91 98765 43210", hash, salt, "2024-01-01 10:00:00")
        )

        // 2. Insert Profile
        db.execSQL(
            """INSERT INTO $TABLE_PROFILES (
                id, user_id, unique_profile_id, full_name, preferred_name, dob, age, gender, blood_group,
                mobile_number, email, address, city, state, country, height_cm, weight_kg,
                profile_photo_url, profile_completion, is_primary, owner_type, relationship,
                organ_donor_status, emergency_pin, qr_secure_token, card_color, show_photo,
                show_blood_group, show_critical_allergy, show_emergency_contact, enable_lanyard, created_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf(
                profileId, userId, uniqueProfileId, "Aarav Patel", "Aarav", "1994-06-18", 32, "Male", "O+",
                "+91 98765 43210", "aarav.patel@example.com", "402, Lotus Heights, Navrangpura", "Ahmedabad",
                "Gujarat", "India", 178, 74,
                "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=250",
                95, 1, "Myself", "Self", "Registered Donor", "7492", "MEDISAFE_SECURE_TOKEN_O9X84A_HASH",
                "classic-navy", 1, 1, 1, 1, 1, "2024-01-01 10:05:00"
            )
        )

        // 3. Emergency Contacts
        db.execSQL(
            """INSERT INTO $TABLE_CONTACTS (id, profile_id, full_name, relationship, mobile_number, alternate_number, email, is_primary, priority_tier, show_on_qr)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf("emg_01", profileId, "Kavita Patel", "Mother", "+91 98250 11223", "+91 79 2640 9876", "kavita.patel@example.com", 1, "Primary", 1)
        )
        db.execSQL(
            """INSERT INTO $TABLE_CONTACTS (id, profile_id, full_name, relationship, mobile_number, alternate_number, email, is_primary, priority_tier, show_on_qr)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf("emg_02", profileId, "Rohan Patel", "Brother", "+91 98980 99887", "", "rohan.patel@example.com", 0, "Secondary", 0)
        )

        // 4. Conditions
        db.execSQL(
            """INSERT INTO $TABLE_CONDITIONS (id, profile_id, name, category, diagnosed_date, status, is_current, is_emergency_relevant, hospital, city, doctor, treatment, stents_count, graft_count, status_desc, symptoms_desc, notes)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf(
                "cond_01", profileId, "Acute Myocardial Infarction (Heart Attack)", "Cardiovascular", "2024-03-14", "VERIFIED", 1, 1,
                "Sunrise Multispeciality Hospital", "Ahmedabad", "Dr. Rahul Shah", "Angioplasty + 1 Drug-Eluting Stent in LAD",
                1, 0, "Currently taking medication", "Occasional exertional breathlessness if missing dose",
                "Single drug-eluting stent placed in proximal LAD. Under quarterly cardiology follow-up."
            )
        )
        db.execSQL(
            """INSERT INTO $TABLE_CONDITIONS (id, profile_id, name, category, diagnosed_date, status, is_current, is_emergency_relevant, hospital, city, doctor, treatment, stents_count, graft_count, status_desc, symptoms_desc, notes)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf(
                "cond_02", profileId, "Essential Hypertension (Stage 1)", "Cardiovascular", "2023-10-10", "VERIFIED", 1, 0,
                "Apex Heart Institute", "Ahmedabad", "Dr. Samir Patel", "Telmisartan 40mg once daily",
                0, 0, "Under regular follow-up", "None", "Blood pressure well controlled (avg 124/82 mmHg)."
            )
        )
        db.execSQL(
            """INSERT INTO $TABLE_CONDITIONS (id, profile_id, name, category, diagnosed_date, status, is_current, is_emergency_relevant, hospital, city, doctor, treatment, stents_count, graft_count, status_desc, symptoms_desc, notes)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf(
                "cond_03", profileId, "Pre-diabetes / Impaired Fasting Glucose", "Endocrine", "2026-08-01", "PENDING_VERIFICATION", 1, 0,
                "Metropolis Healthcare Lab", "Ahmedabad", "Dr. Anita Desai", "Diet & Exercise, Metformin 500mg SOS",
                0, 0, "Lifestyle modification", "None", "HbA1c 6.7%. Repeat testing recommended in 3 months."
            )
        )

        // 5. Allergies
        db.execSQL(
            """INSERT INTO $TABLE_ALLERGIES (id, profile_id, type, name, allergen, reaction, severity, since_when, current_treatment, is_critical, status, notes)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf(
                "alg_01", profileId, "Drug", "Penicillin", "Penicillin", "Severe Anaphylactic Rash & Bronchospasm",
                "Critical / Life Threatening", "2022", "Emergency Epinephrine / Strictly Avoid Beta-Lactams", 1, "VERIFIED",
                "Documented in hospital allergy registry. Life threatening contraindication."
            )
        )
        db.execSQL(
            """INSERT INTO $TABLE_ALLERGIES (id, profile_id, type, name, allergen, reaction, severity, since_when, current_treatment, is_critical, status, notes)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf(
                "alg_02", profileId, "Food", "Shellfish / Prawns", "Shellfish", "Urticaria and facial swelling",
                "Moderate", "2019", "Oral Cetirizine as needed", 0, "VERIFIED",
                "Avoid dietary shellfish."
            )
        )

        // 6. Medications
        db.execSQL(
            """INSERT INTO $TABLE_MEDICATIONS (id, profile_id, name, dosage, frequency, reason, start_date, end_date, prescribed_by, is_critical, status, notes)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf(
                "med_01", profileId, "Aspirin (Cardiprin)", "75 mg", "Once daily (Post lunch)",
                "Antiplatelet therapy following cardiac stent deployment", "2024-03-15", "", "Dr. Rahul Shah", 1, "VERIFIED", "Do not skip."
            )
        )
        db.execSQL(
            """INSERT INTO $TABLE_MEDICATIONS (id, profile_id, name, dosage, frequency, reason, start_date, end_date, prescribed_by, is_critical, status, notes)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf(
                "med_02", profileId, "Ticagrelor (Brilinta)", "90 mg", "Twice daily (Morning & Night)",
                "Dual antiplatelet post-PCI stent maintenance", "2024-03-15", "2025-03-15", "Dr. Rahul Shah", 1, "VERIFIED", "Take with meals."
            )
        )
        db.execSQL(
            """INSERT INTO $TABLE_MEDICATIONS (id, profile_id, name, dosage, frequency, reason, start_date, end_date, prescribed_by, is_critical, status, notes)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf(
                "med_03", profileId, "Telmisartan", "40 mg", "Once daily (Morning)",
                "Essential hypertension management", "2023-10-12", "", "Dr. Samir Patel", 0, "VERIFIED", "Blood pressure maintenance."
            )
        )

        // 7. Medical Reports
        db.execSQL(
            """INSERT INTO $TABLE_REPORTS (id, profile_id, name, type, upload_date, report_date, file_size, file_path, hospital, doctor, related_condition, status, verified_by_patient, notes)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf(
                "doc_01", profileId, "Emergency Discharge Summary - Acute MI & Cath Lab", "Discharge Summary",
                "15 Mar 2024", "14 Mar 2024", "3.4 MB", "", "Sunrise Multispeciality Hospital", "Dr. Rahul Shah",
                "Heart Attack", "VERIFIED", 1, "Discharge protocol following primary PCI to LAD."
            )
        )
        db.execSQL(
            """INSERT INTO $TABLE_REPORTS (id, profile_id, name, type, upload_date, report_date, file_size, file_path, hospital, doctor, related_condition, status, verified_by_patient, notes)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf(
                "doc_02", profileId, "12-Lead Electrocardiogram (Pre-Discharge ECG)", "ECG",
                "16 Mar 2024", "15 Mar 2024", "1.1 MB", "", "Sunrise Multispeciality Hospital", "Dr. Rahul Shah",
                "Heart Attack", "VERIFIED", 1, "Sinus rhythm with evolving T-wave inversions in anterior leads."
            )
        )
        db.execSQL(
            """INSERT INTO $TABLE_REPORTS (id, profile_id, name, type, upload_date, report_date, file_size, file_path, hospital, doctor, related_condition, status, verified_by_patient, notes)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf(
                "doc_03", profileId, "Comprehensive Lipid Profile & HbA1c Panel", "Blood Tests",
                "02 Aug 2026", "01 Aug 2026", "840 KB", "", "Metropolis Healthcare Lab", "Dr. Anita Desai",
                "Pre-diabetes", "VERIFIED", 1, "Total Cholesterol 168 mg/dL, LDL 82 mg/dL, HbA1c 6.7%."
            )
        )

        // 8. Family Members
        db.execSQL(
            """INSERT INTO $TABLE_FAMILY (id, profile_id, full_name, relationship, age, gender, blood_group, photo_url, emergency_card_status, profile_completion, primary_condition, emergency_contact, allergies_count, records_count)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf("fam_01", "prof_kavita_001", "Kavita Patel", "Mother", 58, "Female", "B+", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&q=80&w=250", "Active", 90, "Type 2 Diabetes (Metformin)", "+91 98765 43210 (Aarav)", 0, 8)
        )
        db.execSQL(
            """INSERT INTO $TABLE_FAMILY (id, profile_id, full_name, relationship, age, gender, blood_group, photo_url, emergency_card_status, profile_completion, primary_condition, emergency_contact, allergies_count, records_count)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf("fam_02", "prof_ramesh_001", "Ramesh Patel", "Father", 62, "Male", "O+", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=250", "Active", 85, "Hypertension & Mild Arthritis", "+91 98765 43210 (Aarav)", 1, 5)
        )
        db.execSQL(
            """INSERT INTO $TABLE_FAMILY (id, profile_id, full_name, relationship, age, gender, blood_group, photo_url, emergency_card_status, profile_completion, primary_condition, emergency_contact, allergies_count, records_count)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf("fam_03", "prof_riya_001", "Riya Patel", "Spouse", 30, "Female", "A+", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=250", "Active", 95, "Thyroid (Hypothyroidism)", "+91 98765 43210 (Aarav)", 0, 4)
        )
        db.execSQL(
            """INSERT INTO $TABLE_FAMILY (id, profile_id, full_name, relationship, age, gender, blood_group, photo_url, emergency_card_status, profile_completion, primary_condition, emergency_contact, allergies_count, records_count)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf("fam_04", "prof_dev_001", "Dev Patel", "Sibling", 26, "Male", "B+", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=250", "Active", 100, "No Chronic Illness", "+91 98765 43210 (Aarav)", 0, 2)
        )

        // 9. Access Requests
        db.execSQL(
            """INSERT INTO $TABLE_ACCESS_REQUESTS (id, profile_id, requester_name, requester_role, organization, reason, duration_minutes, status, requested_at, expires_at, pin_required)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf("req_01", profileId, "Dr. Priyanka Nair", "Doctor", "Apollo Emergency Triage (Ahmedabad)", "Evaluating acute chest pain & past stent history", 30, "pending", "Just now", "", 1)
        )

        // 10. Audit Logs
        db.execSQL(
            """INSERT INTO $TABLE_AUDIT_LOGS (id, profile_id, action, requester_name, requester_role, organization, information_accessed, status, timestamp)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf("log_01", profileId, "Emergency QR Scanned", "First Responder (Paramedic Team 4)", "Paramedic", "108 Emergency Medical Services", "Blood Group, Life Threatening Allergies, Emergency Contacts", "Authorized", "Today, 08:30 AM")
        )
        db.execSQL(
            """INSERT INTO $TABLE_AUDIT_LOGS (id, profile_id, action, requester_name, requester_role, organization, information_accessed, status, timestamp)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""",
            arrayOf("log_02", profileId, "Profile Viewed", "Aarav Patel (Patient)", "Self", "Personal App Session", "Medical Profile Summary & Emergency ID", "Authorized", "Yesterday, 07:15 PM")
        )
    }

    // ==========================================
    // USER AUTHENTICATION
    // ==========================================
    fun createUser(fullName: String, email: String, mobile: String, passwordPlain: String): Pair<Boolean, String> {
        val db = writableDatabase
        return try {
            val cursor = db.rawQuery("SELECT id FROM $TABLE_USERS WHERE email = ?", arrayOf(email.trim().lowercase()))
            if (cursor.moveToFirst()) {
                cursor.close()
                return Pair(false, "An account with this email already exists.")
            }
            cursor.close()

            val userId = "usr_" + UUID.randomUUID().toString().take(8)
            val salt = SecurityUtils.generateSalt()
            val hash = SecurityUtils.hashPassword(passwordPlain, salt)
            val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val values = ContentValues().apply {
                put("id", userId)
                put("full_name", fullName.trim())
                put("email", email.trim().lowercase())
                put("mobile_number", mobile.trim())
                put("password_hash", hash)
                put("salt", salt)
                put("created_at", now)
            }
            db.insert(TABLE_USERS, null, values)
            Pair(true, userId)
        } catch (e: Exception) {
            Pair(false, e.message ?: "Database error creating user.")
        }
    }

    fun loginUser(email: String, passwordPlain: String): Pair<Boolean, String> {
        val db = readableDatabase
        return try {
            val cursor = db.rawQuery("SELECT id, password_hash, salt FROM $TABLE_USERS WHERE email = ?", arrayOf(email.trim().lowercase()))
            if (cursor.moveToFirst()) {
                val userId = cursor.getString(0)
                val storedHash = cursor.getString(1)
                val salt = cursor.getString(2)
                cursor.close()

                if (SecurityUtils.verifyPassword(passwordPlain, salt, storedHash)) {
                    Pair(true, userId)
                } else {
                    Pair(false, "Invalid email or password.")
                }
            } else {
                cursor.close()
                Pair(false, "User account not found.")
            }
        } catch (e: Exception) {
            Pair(false, "Login error. Please try again.")
        }
    }

    // ==========================================
    // PROFILES CRUD & ISOLATION
    // ==========================================
    fun getPrimaryProfileForUser(userId: String): UserProfile? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PROFILES WHERE user_id = ? AND is_primary = 1 LIMIT 1", arrayOf(userId))
        val profile = if (cursor.moveToFirst()) cursorToProfile(cursor) else null
        cursor.close()
        return profile ?: getFirstProfile(db)
    }

    fun getProfileById(profileId: String): UserProfile? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PROFILES WHERE id = ? LIMIT 1", arrayOf(profileId))
        val profile = if (cursor.moveToFirst()) cursorToProfile(cursor) else null
        cursor.close()
        return profile
    }

    fun getProfileByUniqueId(uniqueId: String): UserProfile? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PROFILES WHERE unique_profile_id = ? LIMIT 1", arrayOf(uniqueId))
        val profile = if (cursor.moveToFirst()) cursorToProfile(cursor) else null
        cursor.close()
        return profile
    }

    fun getProfileByQrToken(token: String): UserProfile? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PROFILES WHERE qr_secure_token = ? LIMIT 1", arrayOf(token))
        val profile = if (cursor.moveToFirst()) cursorToProfile(cursor) else null
        cursor.close()
        return profile
    }

    private fun getFirstProfile(db: SQLiteDatabase): UserProfile? {
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PROFILES LIMIT 1", null)
        val profile = if (cursor.moveToFirst()) cursorToProfile(cursor) else null
        cursor.close()
        return profile
    }

    fun insertProfile(profile: UserProfile): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("id", profile.id)
                put("user_id", profile.userId)
                put("unique_profile_id", profile.uniqueProfileId)
                put("full_name", profile.fullName)
                put("preferred_name", profile.preferredName)
                put("dob", profile.dateOfBirth)
                put("age", profile.age)
                put("gender", profile.gender)
                put("blood_group", profile.bloodGroup)
                put("mobile_number", profile.mobileNumber)
                put("email", profile.email)
                put("address", profile.address)
                put("city", profile.city)
                put("state", profile.state)
                put("country", profile.country)
                put("height_cm", profile.heightCm)
                put("weight_kg", profile.weightKg)
                put("profile_photo_url", profile.profilePhotoUrl)
                put("profile_completion", profile.profileCompletion)
                put("is_primary", if (profile.isPrimary) 1 else 0)
                put("owner_type", profile.ownerType)
                put("relationship", profile.relationship)
                put("organ_donor_status", profile.organDonorStatus)
                put("emergency_pin", profile.emergencyPin)
                put("qr_secure_token", profile.qrSecureToken)
                put("card_color", profile.cardColor)
                put("show_photo", if (profile.showPhoto) 1 else 0)
                put("show_blood_group", if (profile.showBloodGroup) 1 else 0)
                put("show_critical_allergy", if (profile.showCriticalAllergy) 1 else 0)
                put("show_emergency_contact", if (profile.showEmergencyContact) 1 else 0)
                put("enable_lanyard", if (profile.enableLanyard) 1 else 0)
                put("created_at", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
            }
            db.insert(TABLE_PROFILES, null, values) > 0
        } catch (_: Exception) {
            false
        }
    }

    fun updateProfile(profile: UserProfile): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("full_name", profile.fullName)
                put("preferred_name", profile.preferredName)
                put("dob", profile.dateOfBirth)
                put("age", profile.age)
                put("gender", profile.gender)
                put("blood_group", profile.bloodGroup)
                put("mobile_number", profile.mobileNumber)
                put("email", profile.email)
                put("address", profile.address)
                put("city", profile.city)
                put("state", profile.state)
                put("country", profile.country)
                put("height_cm", profile.heightCm)
                put("weight_kg", profile.weightKg)
                put("profile_photo_url", profile.profilePhotoUrl)
                put("organ_donor_status", profile.organDonorStatus)
                put("emergency_pin", profile.emergencyPin)
                put("qr_secure_token", profile.qrSecureToken)
                put("card_color", profile.cardColor)
                put("show_photo", if (profile.showPhoto) 1 else 0)
                put("show_blood_group", if (profile.showBloodGroup) 1 else 0)
                put("show_critical_allergy", if (profile.showCriticalAllergy) 1 else 0)
                put("show_emergency_contact", if (profile.showEmergencyContact) 1 else 0)
                put("enable_lanyard", if (profile.enableLanyard) 1 else 0)
            }
            db.update(TABLE_PROFILES, values, "id = ?", arrayOf(profile.id)) > 0
        } catch (_: Exception) {
            false
        }
    }

    // ==========================================
    // EMERGENCY CONTACTS CRUD
    // ==========================================
    fun getContactsForProfile(profileId: String): List<EmergencyContact> {
        val list = mutableListOf<EmergencyContact>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CONTACTS WHERE profile_id = ? ORDER BY is_primary DESC", arrayOf(profileId))
        while (cursor.moveToNext()) {
            list.add(
                EmergencyContact(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    profileId = cursor.getString(cursor.getColumnIndexOrThrow("profile_id")),
                    fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                    relationship = cursor.getString(cursor.getColumnIndexOrThrow("relationship")),
                    mobileNumber = cursor.getString(cursor.getColumnIndexOrThrow("mobile_number")),
                    alternateNumber = cursor.getString(cursor.getColumnIndexOrThrow("alternate_number")) ?: "",
                    email = cursor.getString(cursor.getColumnIndexOrThrow("email")) ?: "",
                    isPrimary = cursor.getInt(cursor.getColumnIndexOrThrow("is_primary")) == 1,
                    priorityTier = cursor.getString(cursor.getColumnIndexOrThrow("priority_tier")) ?: "Primary",
                    showOnQr = cursor.getInt(cursor.getColumnIndexOrThrow("show_on_qr")) == 1
                )
            )
        }
        cursor.close()
        return list
    }

    fun insertContact(contact: EmergencyContact): Boolean {
        val db = writableDatabase
        return try {
            if (contact.isPrimary) {
                // Clear existing primary contact flag for this profile
                db.execSQL("UPDATE $TABLE_CONTACTS SET is_primary = 0 WHERE profile_id = ?", arrayOf(contact.profileId))
            }
            val values = ContentValues().apply {
                put("id", contact.id)
                put("profile_id", contact.profileId)
                put("full_name", contact.fullName)
                put("relationship", contact.relationship)
                put("mobile_number", contact.mobileNumber)
                put("alternate_number", contact.alternateNumber)
                put("email", contact.email)
                put("is_primary", if (contact.isPrimary) 1 else 0)
                put("priority_tier", contact.priorityTier)
                put("show_on_qr", if (contact.showOnQr) 1 else 0)
            }
            db.insert(TABLE_CONTACTS, null, values) > 0
        } catch (_: Exception) {
            false
        }
    }

    fun updateContact(contact: EmergencyContact): Boolean {
        val db = writableDatabase
        return try {
            if (contact.isPrimary) {
                db.execSQL("UPDATE $TABLE_CONTACTS SET is_primary = 0 WHERE profile_id = ?", arrayOf(contact.profileId))
            }
            val values = ContentValues().apply {
                put("full_name", contact.fullName)
                put("relationship", contact.relationship)
                put("mobile_number", contact.mobileNumber)
                put("alternate_number", contact.alternateNumber)
                put("email", contact.email)
                put("is_primary", if (contact.isPrimary) 1 else 0)
                put("priority_tier", contact.priorityTier)
                put("show_on_qr", if (contact.showOnQr) 1 else 0)
            }
            db.update(TABLE_CONTACTS, values, "id = ? AND profile_id = ?", arrayOf(contact.id, contact.profileId)) > 0
        } catch (_: Exception) {
            false
        }
    }

    fun deleteContact(contactId: String, profileId: String): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_CONTACTS, "id = ? AND profile_id = ?", arrayOf(contactId, profileId)) > 0
    }

    // ==========================================
    // MEDICAL CONDITIONS CRUD
    // ==========================================
    fun getConditionsForProfile(profileId: String): List<MedicalCondition> {
        val list = mutableListOf<MedicalCondition>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CONDITIONS WHERE profile_id = ? ORDER BY diagnosed_date DESC", arrayOf(profileId))
        while (cursor.moveToNext()) {
            list.add(
                MedicalCondition(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    profileId = cursor.getString(cursor.getColumnIndexOrThrow("profile_id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    category = cursor.getString(cursor.getColumnIndexOrThrow("category")) ?: "Cardiovascular",
                    diagnosedDate = cursor.getString(cursor.getColumnIndexOrThrow("diagnosed_date")) ?: "",
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status")) ?: "VERIFIED",
                    isCurrent = cursor.getInt(cursor.getColumnIndexOrThrow("is_current")) == 1,
                    isEmergencyRelevant = cursor.getInt(cursor.getColumnIndexOrThrow("is_emergency_relevant")) == 1,
                    hospital = cursor.getString(cursor.getColumnIndexOrThrow("hospital")) ?: "",
                    city = cursor.getString(cursor.getColumnIndexOrThrow("city")) ?: "",
                    doctor = cursor.getString(cursor.getColumnIndexOrThrow("doctor")) ?: "",
                    treatment = cursor.getString(cursor.getColumnIndexOrThrow("treatment")) ?: "",
                    stentsCount = cursor.getInt(cursor.getColumnIndexOrThrow("stents_count")),
                    graftCount = cursor.getInt(cursor.getColumnIndexOrThrow("graft_count")),
                    statusDesc = cursor.getString(cursor.getColumnIndexOrThrow("status_desc")) ?: "",
                    symptomsDesc = cursor.getString(cursor.getColumnIndexOrThrow("symptoms_desc")) ?: "",
                    notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")) ?: ""
                )
            )
        }
        cursor.close()
        return list
    }

    fun insertCondition(cond: MedicalCondition): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("id", cond.id)
                put("profile_id", cond.profileId)
                put("name", cond.name)
                put("category", cond.category)
                put("diagnosed_date", cond.diagnosedDate)
                put("status", cond.status)
                put("is_current", if (cond.isCurrent) 1 else 0)
                put("is_emergency_relevant", if (cond.isEmergencyRelevant) 1 else 0)
                put("hospital", cond.hospital)
                put("city", cond.city)
                put("doctor", cond.doctor)
                put("treatment", cond.treatment)
                put("stents_count", cond.stentsCount)
                put("graft_count", cond.graftCount)
                put("status_desc", cond.statusDesc)
                put("symptoms_desc", cond.symptomsDesc)
                put("notes", cond.notes)
            }
            db.insert(TABLE_CONDITIONS, null, values) > 0
        } catch (_: Exception) {
            false
        }
    }

    fun updateCondition(cond: MedicalCondition): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", cond.name)
                put("category", cond.category)
                put("diagnosed_date", cond.diagnosedDate)
                put("status", cond.status)
                put("is_current", if (cond.isCurrent) 1 else 0)
                put("is_emergency_relevant", if (cond.isEmergencyRelevant) 1 else 0)
                put("hospital", cond.hospital)
                put("city", cond.city)
                put("doctor", cond.doctor)
                put("treatment", cond.treatment)
                put("stents_count", cond.stentsCount)
                put("graft_count", cond.graftCount)
                put("status_desc", cond.statusDesc)
                put("symptoms_desc", cond.symptomsDesc)
                put("notes", cond.notes)
            }
            db.update(TABLE_CONDITIONS, values, "id = ? AND profile_id = ?", arrayOf(cond.id, cond.profileId)) > 0
        } catch (_: Exception) {
            false
        }
    }

    fun deleteCondition(conditionId: String, profileId: String): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_CONDITIONS, "id = ? AND profile_id = ?", arrayOf(conditionId, profileId)) > 0
    }

    // ==========================================
    // ALLERGIES CRUD
    // ==========================================
    fun getAllergiesForProfile(profileId: String): List<Allergy> {
        val list = mutableListOf<Allergy>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ALLERGIES WHERE profile_id = ? ORDER BY is_critical DESC", arrayOf(profileId))
        while (cursor.moveToNext()) {
            list.add(
                Allergy(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    profileId = cursor.getString(cursor.getColumnIndexOrThrow("profile_id")),
                    type = cursor.getString(cursor.getColumnIndexOrThrow("type")) ?: "Drug",
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    allergen = cursor.getString(cursor.getColumnIndexOrThrow("allergen")) ?: "",
                    reaction = cursor.getString(cursor.getColumnIndexOrThrow("reaction")),
                    severity = cursor.getString(cursor.getColumnIndexOrThrow("severity")) ?: "Moderate",
                    sinceWhen = cursor.getString(cursor.getColumnIndexOrThrow("since_when")) ?: "",
                    currentTreatment = cursor.getString(cursor.getColumnIndexOrThrow("current_treatment")) ?: "",
                    isCritical = cursor.getInt(cursor.getColumnIndexOrThrow("is_critical")) == 1,
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status")) ?: "VERIFIED",
                    notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")) ?: ""
                )
            )
        }
        cursor.close()
        return list
    }

    fun insertAllergy(allergy: Allergy): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("id", allergy.id)
                put("profile_id", allergy.profileId)
                put("type", allergy.type)
                put("name", allergy.name)
                put("allergen", allergy.allergen)
                put("reaction", allergy.reaction)
                put("severity", allergy.severity)
                put("since_when", allergy.sinceWhen)
                put("current_treatment", allergy.currentTreatment)
                put("is_critical", if (allergy.isCritical) 1 else 0)
                put("status", allergy.status)
                put("notes", allergy.notes)
            }
            db.insert(TABLE_ALLERGIES, null, values) > 0
        } catch (_: Exception) {
            false
        }
    }

    fun updateAllergy(allergy: Allergy): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("type", allergy.type)
                put("name", allergy.name)
                put("allergen", allergy.allergen)
                put("reaction", allergy.reaction)
                put("severity", allergy.severity)
                put("since_when", allergy.sinceWhen)
                put("current_treatment", allergy.currentTreatment)
                put("is_critical", if (allergy.isCritical) 1 else 0)
                put("status", allergy.status)
                put("notes", allergy.notes)
            }
            db.update(TABLE_ALLERGIES, values, "id = ? AND profile_id = ?", arrayOf(allergy.id, allergy.profileId)) > 0
        } catch (_: Exception) {
            false
        }
    }

    fun deleteAllergy(allergyId: String, profileId: String): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_ALLERGIES, "id = ? AND profile_id = ?", arrayOf(allergyId, profileId)) > 0
    }

    // ==========================================
    // MEDICATIONS CRUD
    // ==========================================
    fun getMedicationsForProfile(profileId: String): List<Medication> {
        val list = mutableListOf<Medication>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MEDICATIONS WHERE profile_id = ? ORDER BY is_critical DESC", arrayOf(profileId))
        while (cursor.moveToNext()) {
            list.add(
                Medication(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    profileId = cursor.getString(cursor.getColumnIndexOrThrow("profile_id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    dosage = cursor.getString(cursor.getColumnIndexOrThrow("dosage")),
                    frequency = cursor.getString(cursor.getColumnIndexOrThrow("frequency")),
                    reason = cursor.getString(cursor.getColumnIndexOrThrow("reason")) ?: "",
                    startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date")) ?: "",
                    endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date")) ?: "",
                    prescribedBy = cursor.getString(cursor.getColumnIndexOrThrow("prescribed_by")) ?: "",
                    isCritical = cursor.getInt(cursor.getColumnIndexOrThrow("is_critical")) == 1,
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status")) ?: "VERIFIED",
                    notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")) ?: ""
                )
            )
        }
        cursor.close()
        return list
    }

    fun insertMedication(med: Medication): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("id", med.id)
                put("profile_id", med.profileId)
                put("name", med.name)
                put("dosage", med.dosage)
                put("frequency", med.frequency)
                put("reason", med.reason)
                put("start_date", med.startDate)
                put("end_date", med.endDate)
                put("prescribed_by", med.prescribedBy)
                put("is_critical", if (med.isCritical) 1 else 0)
                put("status", med.status)
                put("notes", med.notes)
            }
            db.insert(TABLE_MEDICATIONS, null, values) > 0
        } catch (_: Exception) {
            false
        }
    }

    fun updateMedication(med: Medication): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", med.name)
                put("dosage", med.dosage)
                put("frequency", med.frequency)
                put("reason", med.reason)
                put("start_date", med.startDate)
                put("end_date", med.endDate)
                put("prescribed_by", med.prescribedBy)
                put("is_critical", if (med.isCritical) 1 else 0)
                put("status", med.status)
                put("notes", med.notes)
            }
            db.update(TABLE_MEDICATIONS, values, "id = ? AND profile_id = ?", arrayOf(med.id, med.profileId)) > 0
        } catch (_: Exception) {
            false
        }
    }

    fun deleteMedication(medId: String, profileId: String): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_MEDICATIONS, "id = ? AND profile_id = ?", arrayOf(medId, profileId)) > 0
    }

    // ==========================================
    // MEDICAL REPORTS CRUD
    // ==========================================
    fun getReportsForProfile(profileId: String): List<MedicalReport> {
        val list = mutableListOf<MedicalReport>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_REPORTS WHERE profile_id = ? ORDER BY id DESC", arrayOf(profileId))
        while (cursor.moveToNext()) {
            list.add(
                MedicalReport(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    profileId = cursor.getString(cursor.getColumnIndexOrThrow("profile_id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
                    uploadDate = cursor.getString(cursor.getColumnIndexOrThrow("upload_date")) ?: "",
                    reportDate = cursor.getString(cursor.getColumnIndexOrThrow("report_date")) ?: "",
                    fileSize = cursor.getString(cursor.getColumnIndexOrThrow("file_size")) ?: "1.0 MB",
                    filePath = cursor.getString(cursor.getColumnIndexOrThrow("file_path")) ?: "",
                    hospital = cursor.getString(cursor.getColumnIndexOrThrow("hospital")) ?: "",
                    doctor = cursor.getString(cursor.getColumnIndexOrThrow("doctor")) ?: "",
                    relatedCondition = cursor.getString(cursor.getColumnIndexOrThrow("related_condition")) ?: "",
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status")) ?: "VERIFIED",
                    verifiedByPatient = cursor.getInt(cursor.getColumnIndexOrThrow("verified_by_patient")) == 1,
                    notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")) ?: ""
                )
            )
        }
        cursor.close()
        return list
    }

    fun insertReport(report: MedicalReport): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("id", report.id)
                put("profile_id", report.profileId)
                put("name", report.name)
                put("type", report.type)
                put("upload_date", report.uploadDate)
                put("report_date", report.reportDate)
                put("file_size", report.fileSize)
                put("file_path", report.filePath)
                put("hospital", report.hospital)
                put("doctor", report.doctor)
                put("related_condition", report.relatedCondition)
                put("status", report.status)
                put("verified_by_patient", if (report.verifiedByPatient) 1 else 0)
                put("notes", report.notes)
            }
            db.insert(TABLE_REPORTS, null, values) > 0
        } catch (_: Exception) {
            false
        }
    }

    fun updateReport(report: MedicalReport): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", report.name)
                put("type", report.type)
                put("report_date", report.reportDate)
                put("file_size", report.fileSize)
                if (report.filePath.isNotBlank()) put("file_path", report.filePath)
                put("hospital", report.hospital)
                put("doctor", report.doctor)
                put("related_condition", report.relatedCondition)
                put("status", report.status)
                put("notes", report.notes)
            }
            db.update(TABLE_REPORTS, values, "id = ? AND profile_id = ?", arrayOf(report.id, report.profileId)) > 0
        } catch (_: Exception) {
            false
        }
    }

    fun deleteReport(reportId: String, profileId: String): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_REPORTS, "id = ? AND profile_id = ?", arrayOf(reportId, profileId)) > 0
    }

    // ==========================================
    // FAMILY MEMBERS
    // ==========================================
    fun getFamilyMembers(): List<FamilyMember> {
        val list = mutableListOf<FamilyMember>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_FAMILY", null)
        while (cursor.moveToNext()) {
            list.add(
                FamilyMember(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    profileId = cursor.getString(cursor.getColumnIndexOrThrow("profile_id")),
                    fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                    relationship = cursor.getString(cursor.getColumnIndexOrThrow("relationship")),
                    age = cursor.getInt(cursor.getColumnIndexOrThrow("age")),
                    gender = cursor.getString(cursor.getColumnIndexOrThrow("gender")),
                    bloodGroup = cursor.getString(cursor.getColumnIndexOrThrow("blood_group")),
                    photoUrl = cursor.getString(cursor.getColumnIndexOrThrow("photo_url")) ?: "",
                    emergencyCardStatus = cursor.getString(cursor.getColumnIndexOrThrow("emergency_card_status")) ?: "Active",
                    profileCompletion = cursor.getInt(cursor.getColumnIndexOrThrow("profile_completion")),
                    primaryCondition = cursor.getString(cursor.getColumnIndexOrThrow("primary_condition")) ?: "",
                    emergencyContact = cursor.getString(cursor.getColumnIndexOrThrow("emergency_contact")) ?: "",
                    allergiesCount = cursor.getInt(cursor.getColumnIndexOrThrow("allergies_count")),
                    recordsCount = cursor.getInt(cursor.getColumnIndexOrThrow("records_count"))
                )
            )
        }
        cursor.close()
        return list
    }

    fun insertFamilyMember(member: FamilyMember): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("id", member.id)
                put("profile_id", member.profileId)
                put("full_name", member.fullName)
                put("relationship", member.relationship)
                put("age", member.age)
                put("gender", member.gender)
                put("blood_group", member.bloodGroup)
                put("photo_url", member.photoUrl)
                put("emergency_card_status", member.emergencyCardStatus)
                put("profile_completion", member.profileCompletion)
                put("primary_condition", member.primaryCondition)
                put("emergency_contact", member.emergencyContact)
                put("allergies_count", member.allergiesCount)
                put("records_count", member.recordsCount)
            }
            db.insert(TABLE_FAMILY, null, values) > 0
        } catch (_: Exception) {
            false
        }
    }

    // ==========================================
    // ACCESS REQUESTS & AUDIT LOGS
    // ==========================================
    fun getAccessRequestsForProfile(profileId: String): List<AccessRequest> {
        val list = mutableListOf<AccessRequest>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ACCESS_REQUESTS WHERE profile_id = ? ORDER BY id DESC", arrayOf(profileId))
        while (cursor.moveToNext()) {
            list.add(
                AccessRequest(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    profileId = cursor.getString(cursor.getColumnIndexOrThrow("profile_id")),
                    requesterName = cursor.getString(cursor.getColumnIndexOrThrow("requester_name")),
                    requesterRole = cursor.getString(cursor.getColumnIndexOrThrow("requester_role")),
                    organization = cursor.getString(cursor.getColumnIndexOrThrow("organization")),
                    reason = cursor.getString(cursor.getColumnIndexOrThrow("reason")),
                    durationMinutes = cursor.getInt(cursor.getColumnIndexOrThrow("duration_minutes")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                    requestedAt = cursor.getString(cursor.getColumnIndexOrThrow("requested_at")) ?: "",
                    expiresAt = cursor.getString(cursor.getColumnIndexOrThrow("expires_at")) ?: "",
                    pinRequired = cursor.getInt(cursor.getColumnIndexOrThrow("pin_required")) == 1
                )
            )
        }
        cursor.close()
        return list
    }

    fun updateAccessRequestStatus(requestId: String, status: String, expiresAt: String = ""): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("status", status)
                if (expiresAt.isNotBlank()) put("expires_at", expiresAt)
            }
            db.update(TABLE_ACCESS_REQUESTS, values, "id = ?", arrayOf(requestId)) > 0
        } catch (_: Exception) {
            false
        }
    }

    fun getAuditLogsForProfile(profileId: String): List<AuditLogEntry> {
        val list = mutableListOf<AuditLogEntry>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_AUDIT_LOGS WHERE profile_id = ? ORDER BY id DESC", arrayOf(profileId))
        while (cursor.moveToNext()) {
            list.add(
                AuditLogEntry(
                    id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    profileId = cursor.getString(cursor.getColumnIndexOrThrow("profile_id")),
                    action = cursor.getString(cursor.getColumnIndexOrThrow("action")),
                    requesterName = cursor.getString(cursor.getColumnIndexOrThrow("requester_name")),
                    requesterRole = cursor.getString(cursor.getColumnIndexOrThrow("requester_role")),
                    organization = cursor.getString(cursor.getColumnIndexOrThrow("organization")),
                    informationAccessed = cursor.getString(cursor.getColumnIndexOrThrow("information_accessed")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                    timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
                )
            )
        }
        cursor.close()
        return list
    }

    fun insertAuditLog(log: AuditLogEntry): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("id", log.id)
                put("profile_id", log.profileId)
                put("action", log.action)
                put("requester_name", log.requesterName)
                put("requester_role", log.requesterRole)
                put("organization", log.organization)
                put("information_accessed", log.informationAccessed)
                put("status", log.status)
                put("timestamp", log.timestamp)
            }
            db.insert(TABLE_AUDIT_LOGS, null, values) > 0
        } catch (_: Exception) {
            false
        }
    }

    // Helper to map cursor to UserProfile
    private fun cursorToProfile(cursor: Cursor): UserProfile {
        return UserProfile(
            id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
            userId = cursor.getString(cursor.getColumnIndexOrThrow("user_id")),
            uniqueProfileId = cursor.getString(cursor.getColumnIndexOrThrow("unique_profile_id")),
            fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
            preferredName = cursor.getString(cursor.getColumnIndexOrThrow("preferred_name")) ?: "",
            dateOfBirth = cursor.getString(cursor.getColumnIndexOrThrow("dob")) ?: "",
            age = cursor.getInt(cursor.getColumnIndexOrThrow("age")),
            gender = cursor.getString(cursor.getColumnIndexOrThrow("gender")) ?: "Male",
            bloodGroup = cursor.getString(cursor.getColumnIndexOrThrow("blood_group")) ?: "O+",
            mobileNumber = cursor.getString(cursor.getColumnIndexOrThrow("mobile_number")) ?: "",
            email = cursor.getString(cursor.getColumnIndexOrThrow("email")) ?: "",
            address = cursor.getString(cursor.getColumnIndexOrThrow("address")) ?: "",
            city = cursor.getString(cursor.getColumnIndexOrThrow("city")) ?: "",
            state = cursor.getString(cursor.getColumnIndexOrThrow("state")) ?: "",
            country = cursor.getString(cursor.getColumnIndexOrThrow("country")) ?: "India",
            heightCm = cursor.getInt(cursor.getColumnIndexOrThrow("height_cm")),
            weightKg = cursor.getInt(cursor.getColumnIndexOrThrow("weight_kg")),
            profilePhotoUrl = cursor.getString(cursor.getColumnIndexOrThrow("profile_photo_url")) ?: "",
            profileCompletion = cursor.getInt(cursor.getColumnIndexOrThrow("profile_completion")),
            isPrimary = cursor.getInt(cursor.getColumnIndexOrThrow("is_primary")) == 1,
            ownerType = cursor.getString(cursor.getColumnIndexOrThrow("owner_type")) ?: "Myself",
            relationship = cursor.getString(cursor.getColumnIndexOrThrow("relationship")) ?: "Self",
            organDonorStatus = cursor.getString(cursor.getColumnIndexOrThrow("organ_donor_status")) ?: "Registered Donor",
            emergencyPin = cursor.getString(cursor.getColumnIndexOrThrow("emergency_pin")) ?: "7492",
            qrSecureToken = cursor.getString(cursor.getColumnIndexOrThrow("qr_secure_token")) ?: "",
            cardColor = cursor.getString(cursor.getColumnIndexOrThrow("card_color")) ?: "classic-navy",
            showPhoto = cursor.getInt(cursor.getColumnIndexOrThrow("show_photo")) == 1,
            showBloodGroup = cursor.getInt(cursor.getColumnIndexOrThrow("show_blood_group")) == 1,
            showCriticalAllergy = cursor.getInt(cursor.getColumnIndexOrThrow("show_critical_allergy")) == 1,
            showEmergencyContact = cursor.getInt(cursor.getColumnIndexOrThrow("show_emergency_contact")) == 1,
            enableLanyard = cursor.getInt(cursor.getColumnIndexOrThrow("enable_lanyard")) == 1
        )
    }
}
