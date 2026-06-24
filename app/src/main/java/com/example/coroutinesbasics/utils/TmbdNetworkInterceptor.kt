package com.example.coroutinesbasics.utils

import com.example.coroutinesbasics.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class TmbdNetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Architect Check: If the route is explicitly marked with @NoAuth, pass it through
        /*val invocation = originalRequest.tag(retrofit2.Invocation::class.java)
        val hasNoAuthAnnotation = invocation?.method()?.getAnnotation(NoAuth::class.java) != null

        if (hasNoAuthAnnotation) {
            return chain.proceed(originalRequest)
        }*/

        //val token = tokenProvider.getAccessToken()

        // If no token exists, proceed normally (let the server reject it or handle public view)
        /*if (token.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }*/

        // Clean Header Injection
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer ${BuildConfig.TMDB_API_KEY}")
            .header("Accept", "application/json")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}