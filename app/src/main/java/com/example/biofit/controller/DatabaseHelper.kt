package com.example.biofit.controller

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.biofit.model.AIExercise
import com.example.biofit.model.UserData
import com.example.biofit.model.UserPlan

class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = """
            CREATE TABLE User (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                fullName TEXT,
                email TEXT,
                password TEXT,
                gender INTEGER CHECK (gender IN (0, 1)),
                dateOfBirth TEXT,
                height REAL,
                weight REAL,
                targetWeight REAL
            )
        """.trimIndent()
// hehe
        val createUserPlan = """
            CREATE TABLE UserPlan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER REFERENCES User(id),
                goal INTEGER CHECK (goal IN (0, 1, 2)),
                planDuration INTEGER CHECK (planDuration > 0),
                diet INTEGER CHECK (diet IN (0, 1)),
                workoutIntensity INTEGER CHECK (workoutIntensity IN (0, 1, 2)),
                state INTEGER CHECK (state IN (0, 1))
            )
        """.trimIndent()

        val createAIExercise = """
            CREATE TABLE AIExercise (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userPlanId INTEGER REFERENCES UserPlan(id),
                session INTEGER CHECK (session IN (0, 1, 2)),
                exerciseName TEXT,
                duration INTEGER,
                caloriesBurned REAL,
                intensity INTEGER CHECK (intensity IN (0, 1, 2))
            )
        """.trimIndent()

        db.execSQL(createUserPlan)
        db.execSQL(createUserTable)
        db.execSQL(createAIExercise)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS User")
        db.execSQL("DROP TABLE IF EXISTS UserPlan")
        db.execSQL("DROP TABLE IF EXISTS AIExercise")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "BioFit.db"
        private const val DATABASE_VERSION = 1
    }
}

class DatabaseHelper(context: Context) {
    private val dbHelper = SQLiteHelper(context)

    fun getUserDataById(userId: Int): UserData? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM User WHERE id = ?", arrayOf(userId.toString()))
        var userData: UserData? = null
        if (cursor.moveToFirst()) {
            userData = UserData(
                id = cursor.getInt(0),
                fullName = cursor.getString(1),
                email = cursor.getString(2),
                password = cursor.getString(3),
                gender = cursor.getInt(4),
                dateOfBirth = cursor.getString(5),
                height = cursor.getFloat(6),
                weight = cursor.getFloat(7),
                targetWeight = cursor.getFloat(8)
            )
            Log.d("Database", "Dữ liệu người dùng: $userData") // Xem dữ liệu có được lấy ra không
        } else {
            Log.d("Database", "Không có dữ liệu trong User")
        }
        cursor.close()
        db.close()
        return userData
    }

    fun addUserData(userData: UserData) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("fullName", userData.fullName)
            put("email", userData.email)
            put("password", userData.password)
            put("gender", userData.gender)
            put("dateOfBirth", userData.dateOfBirth)
            put("height", userData.height)
            put("weight", userData.weight)
            put("targetWeight", userData.targetWeight)
        }
        db.insert("User", null, values)
        db.close()
    }

    fun updateUserData(userData: UserData) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("fullName", userData.fullName)
            put("email", userData.email)
            put("password", userData.password)
            put("gender", userData.gender)
            put("dateOfBirth", userData.dateOfBirth)
            put("height", userData.height)
            put("weight", userData.weight)
            put("targetWeight", userData.targetWeight)
        }
        db.update("User", values, "id = ?", arrayOf(userData.id.toString()))
        db.close()
    }

    fun deleteUserData(userId: Int) {
        val db = dbHelper.writableDatabase
        db.delete("User", "id = ?", arrayOf(userId.toString()))
        db.close()
    }

    fun getUserPlanById(userId: Int, state: Int): UserPlan? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM UserPlan WHERE userId = ? AND state = ?",
            arrayOf(userId.toString(), state.toString())
        )
        var userPlan: UserPlan? = null
        if (cursor.moveToFirst()) {
            userPlan = UserPlan(
                id = cursor.getInt(0),
                userId = cursor.getInt(1),
                goal = cursor.getInt(2),
                planDuration = cursor.getInt(3),
                diet = cursor.getInt(4),
                workoutIntensity = cursor.getInt(5),
                status = cursor.getInt(6)
            )
            Log.d(
                "Database",
                "Dữ liệu kế hoạch người dùng: $userPlan"
            ) // Xem dữ liệu có được lấy ra không
        } else {
            Log.d("Database", "Không có dữ liệu trong UserPlan")
        }
        cursor.close()
        db.close()
        return userPlan
    }

    fun addUserPlan(userPlan: UserPlan) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("userId", userPlan.userId)
            put("goal", userPlan.goal)
            put("planDuration", userPlan.planDuration)
            put("diet", userPlan.diet)
            put("workoutIntensity", userPlan.workoutIntensity)
            put("state", userPlan.status)
        }
        db.insert("UserPlan", null, values)
        db.close()
    }

    fun addAIExercise(aiExercise: AIExercise) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("userPlanId", aiExercise.userPlanId)
            put("exerciseName", aiExercise.exerciseName)
            put("duration", aiExercise.duration)
            put("caloriesBurned", aiExercise.caloriesBurned)
            put("intensity", aiExercise.intensity)
        }
        db.insert("AIExercise", null, values)
        db.close()
    }

    fun getAIExercisesByUserPlanId(userPlanId: Int): List<AIExercise> {
        val aiExercises = mutableListOf<AIExercise>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM AIExercise WHERE userPlanId = ?",
            arrayOf(userPlanId.toString())
        )
        while (cursor.moveToNext()) {
            val aiExercise = AIExercise(
                id = cursor.getInt(0),
                userPlanId = cursor.getInt(1),
                session = cursor.getInt(2),
                exerciseName = cursor.getString(3),
                duration = cursor.getInt(4),
                caloriesBurned = cursor.getFloat(5),
                intensity = cursor.getInt(6)
                )
            aiExercises.add(aiExercise)
            }
        cursor.close()
        db.close()
        return aiExercises
    }
}