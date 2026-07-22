package com.example.util

import com.example.data.local.CitationEntity
import kotlin.math.pow
import kotlin.math.sqrt

data class StatisticsResult(
    val numbers: List<Double>,
    val count: Int,
    val mean: Double,
    val median: Double,
    val stdDev: Double,
    val min: Double,
    val max: Double
)

object AcademicUtils {

    fun parseAndCalculateStats(input: String): StatisticsResult? {
        val numbers = input.split(",", " ", "\n", ";")
            .mapNotNull { it.trim().toDoubleOrNull() }

        if (numbers.isEmpty()) return null

        val count = numbers.size
        val mean = numbers.average()
        val sorted = numbers.sorted()
        
        val median = if (count % 2 == 0) {
            (sorted[count / 2 - 1] + sorted[count / 2]) / 2.0
        } else {
            sorted[count / 2]
        }

        val variance = numbers.sumOf { (it - mean).pow(2) } / count
        val stdDev = sqrt(variance)

        return StatisticsResult(
            numbers = numbers,
            count = count,
            mean = mean,
            median = median,
            stdDev = stdDev,
            min = sorted.first(),
            max = sorted.last()
        )
    }

    fun formatApa(authors: String, title: String, year: String, publisherOrJournal: String, doiOrUrl: String): String {
        val yr = if (year.isBlank()) "n.d." else year
        val pub = if (publisherOrJournal.isBlank()) "" else " $publisherOrJournal."
        val link = if (doiOrUrl.isBlank()) "" else " https://doi.org/$doiOrUrl"
        return "$authors ($yr). $title.$pub$link"
    }

    fun formatIeee(authors: String, title: String, year: String, publisherOrJournal: String, doiOrUrl: String): String {
        val yr = if (year.isBlank()) "" else ", $year"
        val pub = if (publisherOrJournal.isBlank()) "" else ", $publisherOrJournal"
        val link = if (doiOrUrl.isBlank()) "" else ", doi: $doiOrUrl"
        return "$authors, \"$title,\"$pub$yr$link."
    }

    fun formatMla(authors: String, title: String, year: String, publisherOrJournal: String, doiOrUrl: String): String {
        val yr = if (year.isBlank()) "" else " $year."
        val pub = if (publisherOrJournal.isBlank()) "" else " $publisherOrJournal,"
        val link = if (doiOrUrl.isBlank()) "" else " $doiOrUrl."
        return "$authors. \"$title.\"$pub$yr$link"
    }

    fun formatHarvard(authors: String, title: String, year: String, publisherOrJournal: String, doiOrUrl: String): String {
        val yr = if (year.isBlank()) "n.d." else year
        val pub = if (publisherOrJournal.isBlank()) "" else " $publisherOrJournal."
        val link = if (doiOrUrl.isBlank()) "" else " Available at: $doiOrUrl"
        return "$authors ($yr) '$title',$pub$link"
    }
}
