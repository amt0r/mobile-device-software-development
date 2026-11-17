package com.example.emergencypower.data.remote

import okhttp3.*

class MockScadaInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url()
        val path = url.encodedPath()

        val forceError = url.queryParameter("force_error")?.toIntOrNull()
        if (forceError != null && forceError >= 400) {
            return createErrorResponse(request, forceError, "Simulated SCADA Error HTTP $forceError")
        }

        when {
            path.endsWith("api/v1/grid/status") -> {
                val json = """
                    {
                        "station_id": "SUBSTATION-18-KYIV",
                        "timestamp": ${System.currentTimeMillis()},
                        "grid_voltage": 224.8,
                        "frequency_hz": 50.02,
                        "current_load_kw": 48.6,
                        "active_source": "Основна мережа 230В",
                        "backup_ready": true,
                        "battery_soc_percent": 94
                    }
                """.trimIndent()
                return createSuccessResponse(request, json)
            }

            path.endsWith("api/v1/telemetry/forecast") -> {
                val json = """
                    {
                        "substation_name": "ПС-110/10 «Київська Енергетична» (Вар. 18)",
                        "generated_at": ${System.currentTimeMillis()},
                        "forecast_items": [
                            {"hour_offset": 1, "predicted_kw": 52.4, "risk_level": "LOW"},
                            {"hour_offset": 2, "predicted_kw": 58.1, "risk_level": "MODERATE"},
                            {"hour_offset": 3, "predicted_kw": 74.5, "risk_level": "HIGH"},
                            {"hour_offset": 4, "predicted_kw": 68.2, "risk_level": "HIGH"},
                            {"hour_offset": 5, "predicted_kw": 49.0, "risk_level": "LOW"},
                            {"hour_offset": 6, "predicted_kw": 44.3, "risk_level": "LOW"}
                        ]
                    }
                """.trimIndent()
                return createSuccessResponse(request, json)
            }

            path.endsWith("api/v1/auth/login") -> {
                val json = """
                    {
                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxOC1BRE1JTiIsInJvbGUiOiJBRE1JTiJ9.mocked_signature_pw3",
                        "role": "ADMIN",
                        "expires_in_seconds": 3600
                    }
                """.trimIndent()
                return createSuccessResponse(request, json)
            }

            path.endsWith("api/v1/generator/control") -> {
                val auth = request.header("Authorization")
                if (auth.isNullOrBlank() || !auth.startsWith("Bearer ")) {
                    return createErrorResponse(request, 401, "Необхідна автентифікація диспетчера")
                }
                if (!auth.contains("ADMIN")) {
                    return createErrorResponse(request, 403, "Недостатньо прав для примусового пуску генератора")
                }
                val json = """
                    {
                        "success": true,
                        "message": "Команда успішно виконана: дизель-генератор ДГ-50 переведено в робочий режим."
                    }
                """.trimIndent()
                return createSuccessResponse(request, json)
            }

            else -> {
                return chain.proceed(request)
            }
        }
    }

    private fun createSuccessResponse(request: Request, json: String): Response {
        val mediaType = MediaType.parse("application/json; charset=utf-8")
        val body = ResponseBody.create(mediaType, json)
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(body)
            .build()
    }

    private fun createErrorResponse(request: Request, code: Int, message: String): Response {
        val json = """{"error": "$message", "code": $code}"""
        val mediaType = MediaType.parse("application/json; charset=utf-8")
        val body = ResponseBody.create(mediaType, json)
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(message)
            .body(body)
            .build()
    }
}
