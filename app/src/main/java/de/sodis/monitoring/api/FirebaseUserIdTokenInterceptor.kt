package de.sodis.monitoring.api

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class FirebaseUserIdTokenInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        try {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                throw Exception("User is not logged in.")
            } else {
                val task = user.getIdToken(true)
                val tokenResult = Tasks.await(task)
                val idToken = tokenResult.token

                if (idToken == null) {
                    throw Exception("idToken is null")
                } else {
                    val modifiedRequest = request.newBuilder()
                        .addHeader(X_FIREBASE_ID_TOKEN, idToken)
                        .build()
                    return chain.proceed(modifiedRequest)
                }
            }
        } catch (e: Exception) {
            throw IOException(e.message)
        }

    }

    companion object {

        // Custom header for passing ID token in request.
            private val X_FIREBASE_ID_TOKEN = "X-Authorization-Firebase"
    }
}