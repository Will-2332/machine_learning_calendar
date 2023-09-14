package com.example.machine_learning_calendar

data class CleansedEvent(
    val title: Int,
    val location: Int,
    val startTime: Float,
    val endTime: Float,
    val duration: Float,
    val grade: Int,
    val grade1: Int
)

class DataCleaner {
    fun cleanData(events: List<Event>): List<CleansedEvent> {
        return events.map { event ->
            // Handle missing values
            if (event.title == null || event.location == null) {
                return@map null
            }

            // Convert categorical data to numerical data
            val titleNumerical = event.title.hashCode()
            val locationNumerical = event.location.hashCode()

            // Normalize numerical data
            val startTimeNormalized = normalizeTime(event.startTime)
            val endTimeNormalized = normalizeTime(event.endTime)

            // Feature engineering
            val duration = calculateDuration(event.startTime, event.endTime)

            // Convert suggestion to numerical data
            val suggestionNumerical = if (event.suggestion.isEmpty()) 0 else 1

            CleansedEvent(titleNumerical, locationNumerical, startTimeNormalized, endTimeNormalized, duration, suggestionNumerical, event.grade)
        }.filterNotNull()
    }


    private fun normalizeTime(time: String): Float {
        val (hour, minute) = time.split(":").map { it.toInt() }
        return hour + minute / 60.0f
    }

    private fun calculateDuration(startTime: String, endTime: String): Float {
        return normalizeTime(endTime) - normalizeTime(startTime)
    }
}
