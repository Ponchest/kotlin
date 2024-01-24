/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.build.report.statistics.file

import org.jetbrains.kotlin.build.report.metrics.*
import org.jetbrains.kotlin.build.report.statistics.*
import org.jetbrains.kotlin.build.report.statistics.asString
import org.jetbrains.kotlin.build.report.statistics.formatTime
import java.io.File
import java.util.*

data class ReadableFileReportData<B : BuildTime, P : BuildPerformanceMetric>(
    val statisticsData: List<CompileStatisticsData<B, P>>,
    val startParameters: BuildStartParameters,
    val failureMessages: List<String> = emptyList(),
)

open class ReadableFileReportService<B : BuildTime, P : BuildPerformanceMetric>(
    buildReportDir: File,
    projectName: String,
    private val printMetrics: Boolean,
) : FileReportService<ReadableFileReportData<B, P>>(buildReportDir, projectName, "txt") {

    open fun printCustomTaskMetrics(statisticsData: CompileStatisticsData<B, P>, printer: Printer) {}

    override fun printBuildReport(data: ReadableFileReportData<B, P>, outputFile: File) {
        outputFile.bufferedWriter().use { writer ->
            printBuildReport(data, Printer(writer))
        }
    }

    fun printBuildReport(data: ReadableFileReportData<B, P>, printer: Printer) {
        // NOTE: BuildExecutionData / BuildOperationRecord contains data for both tasks and transforms.
        // Where possible, we still use the term "tasks" because saying "tasks/transforms" is a bit verbose and "build operations" may sound
        // a bit unfamiliar.
        // TODO: If it is confusing, consider renaming "tasks" to "build operations" in this class.
        printBuildInfo(data.startParameters, data.failureMessages, printer)
        if (printMetrics && data.statisticsData.isNotEmpty()) {
            printMetrics(
                data.statisticsData.map { it.getBuildTimesMetrics() }.reduce { agg, value ->
                    (agg.keys + value.keys).associateWith { (agg[it] ?: 0) + (value[it] ?: 0) }
                },
                data.statisticsData.map { it.getPerformanceMetrics() }.reduce { agg, value ->
                    (agg.keys + value.keys).associateWith { (agg[it] ?: 0) + (value[it] ?: 0) }
                },
                data.statisticsData.map { it.getNonIncrementalAttributes().asSequence() }.reduce { agg, value -> agg + value }.toList(),
                aggregatedMetric = true,
                printer = printer,
            )
            printer.println()
        }
        printTaskOverview(data.statisticsData, printer)
        printTasksLog(data.statisticsData, printer)
    }

    private fun printBuildInfo(startParameters: BuildStartParameters, failureMessages: List<String>, printer: Printer) {
        printer.withIndent("Gradle start parameters:") {
            startParameters.let {
                printer.println("tasks = ${it.tasks}")
                printer.println("excluded tasks = ${it.excludedTasks}")
                printer.println("current dir = ${it.currentDir}")
                printer.println("project properties args = ${it.projectProperties}")
                printer.println("system properties args = ${it.systemProperties}")
            }
        }
        printer.println()

        if (failureMessages.isNotEmpty()) {
            printer.println("Build failed: ${failureMessages}")
            printer.println()
        }
    }

    private fun printMetrics(
        buildTimesMetrics: Map<out BuildTime, Long>,
        performanceMetrics: Map<out BuildPerformanceMetric, Long>,
        nonIncrementalAttributes: Collection<BuildAttribute>,
        gcTimeMetrics: Map<String, Long>? = emptyMap(),
        gcCountMetrics: Map<String, Long>? = emptyMap(),
        aggregatedMetric: Boolean = false,
        printer: Printer,
    ) {
        printBuildTimes(buildTimesMetrics, printer)
        if (aggregatedMetric) printer.println()

        printBuildPerformanceMetrics(performanceMetrics, printer)
        if (aggregatedMetric) printer.println()

        printBuildAttributes(nonIncrementalAttributes, printer)

        //TODO: KT-57310 Implement build GC metric in
        if (!aggregatedMetric) {
            printGcMetrics(gcTimeMetrics, gcCountMetrics, printer)
        }
    }

    private fun printGcMetrics(
        gcTimeMetrics: Map<String, Long>?,
        gcCountMetrics: Map<String, Long>?,
        printer: Printer,
    ) {
        val keys = HashSet<String>()
        gcCountMetrics?.keys?.also { keys.addAll(it) }
        gcTimeMetrics?.keys?.also { keys.addAll(it) }
        if (keys.isEmpty()) return

        printer.withIndent("GC metrics:") {
            for (key in keys) {
                printer.println("$key:")
                printer.withIndent {
                    gcCountMetrics?.get(key)?.also { printer.println("GC count: ${it}") }
                    gcTimeMetrics?.get(key)?.also { printer.println("GC time: ${formatTime(it)}") }
                }
            }
        }
    }

    private fun printBuildTimes(buildTimes: Map<out BuildTime, Long>, printer: Printer) {
        if (buildTimes.isEmpty()) return

        printer.println("Time metrics:")
        printer.withIndent {
            val visitedBuildTimes = HashSet<BuildTime>()
            fun printBuildTime(buildTime: BuildTime) {
                if (!visitedBuildTimes.add(buildTime)) return

                val timeMs = buildTimes[buildTime]
                if (timeMs != null) {
                    printer.println("${buildTime.getReadableString()}: ${formatTime(timeMs)}")
                    printer.withIndent {
                        buildTime.children()?.forEach { printBuildTime(it) }
                    }
                } else {
                    //Skip formatting if parent metric does not set
                    buildTime.children()?.forEach { printBuildTime(it) }
                }
            }

            for (buildTime in buildTimes.keys.first().getAllMetrics()) {
                if (buildTime.getParent() != null) continue

                printBuildTime(buildTime)
            }
        }
    }

    private fun printBuildPerformanceMetrics(buildMetrics: Map<out BuildPerformanceMetric, Long>, printer: Printer) {
        if (buildMetrics.isEmpty()) return

        printer.withIndent("Size metrics:") {
            for (metric in buildMetrics.keys.first().getAllMetrics()) {
                buildMetrics[metric]?.let { printSizeMetric(metric, it, printer) }
            }
        }
    }

    private fun printSizeMetric(sizeMetric: BuildPerformanceMetric, value: Long, printer: Printer) {
        fun BuildPerformanceMetric.numberOfAncestors(): Int {
            var count = 0
            var parent: BuildPerformanceMetric? = getParent()
            while (parent != null) {
                count++
                parent = parent.getParent()
            }
            return count
        }

        val indentLevel = sizeMetric.numberOfAncestors()

        repeat(indentLevel) { printer.pushIndent() }
        when (sizeMetric.getType()) {
            ValueType.BYTES -> printer.println("${sizeMetric.getReadableString()}: ${formatSize(value)}")
            ValueType.NUMBER -> printer.println("${sizeMetric.getReadableString()}: $value")
            ValueType.NANOSECONDS -> printer.println("${sizeMetric.getReadableString()}: $value")
            ValueType.MILLISECONDS -> printer.println("${sizeMetric.getReadableString()}: ${formatTime(value)}")
            ValueType.TIME -> printer.println("${sizeMetric.getReadableString()}: ${formatter.format(value)}")
        }
        repeat(indentLevel) { printer.popIndent() }
    }

    private fun printBuildAttributes(buildAttributes: Collection<BuildAttribute>, printer: Printer) {
        if (buildAttributes.isEmpty()) return

        val buildAttributesMap = buildAttributes.groupingBy { it }.eachCount()
        printer.withIndent("Build attributes:") {
            val attributesByKind = buildAttributesMap.entries.groupBy { it.key.kind }.toSortedMap()
            for ((kind, attributesCounts) in attributesByKind) {
                printMap(printer, kind.name, attributesCounts.associate { (k, v) -> k.readableString to v })
            }
        }
    }

    private fun printTaskOverview(statisticsData: Collection<CompileStatisticsData<B, P>>, printer: Printer) {
        var allTasksTimeMs = 0L
        var kotlinTotalTimeMs = 0L
        val kotlinTasks = ArrayList<CompileStatisticsData<B, P>>()

        for (task in statisticsData) {
            val taskTimeMs = task.getDurationMs()
            allTasksTimeMs += taskTimeMs

            if (task.getFromKotlinPlugin() == true) {
                kotlinTotalTimeMs += taskTimeMs
                kotlinTasks.add(task)
            }
        }

        if (kotlinTasks.isEmpty()) {
            printer.println("No Kotlin task was run")
            return
        }

        val ktTaskPercent = (kotlinTotalTimeMs.toDouble() / allTasksTimeMs * 100).asString(1)
        printer.println("Total time for Kotlin tasks: ${formatTime(kotlinTotalTimeMs)} ($ktTaskPercent % of all tasks time)")

        val table = TextTable("Time", "% of Kotlin time", "Task")
        for (task in kotlinTasks.sortedWith(compareBy({ -it.getDurationMs() }, { it.getStartTimeMs() }))) {
            val timeMs = task.getDurationMs()
            val percent = (timeMs.toDouble() / kotlinTotalTimeMs * 100).asString(1)
            table.addRow(formatTime(timeMs), "$percent %", task.getTaskName())
        }
        table.printTo(printer)
        printer.println()
    }

    private fun printTasksLog(
        statisticsData: List<CompileStatisticsData<B, P>>,
        printer: Printer,
    ) {
        for (task in statisticsData.sortedWith(compareBy({ -it.getDurationMs() }, { it.getStartTimeMs() }))) {
            printTaskLog(task, printer)
            printer.println()
        }
    }

    private fun printTaskLog(
        statisticsData: CompileStatisticsData<B, P>,
        printer: Printer,
    ) {
        val skipMessage = statisticsData.getSkipMessage()
        if (skipMessage != null) {
            printer.println("Task '${statisticsData.getTaskName()}' was skipped: $skipMessage")
        } else {
            printer.println("Task '${statisticsData.getTaskName()}' finished in ${formatTime(statisticsData.getDurationMs())}")
        }

        statisticsData.getKotlinLanguageVersion()?.also {
            printer.withIndent("Task info:") {
                printer.println("Kotlin language version: $it")
            }
        }

        if (statisticsData.getIcLogLines().isNotEmpty()) {
            printer.withIndent("Compilation log for task '${statisticsData.getTaskName()}':") {
                statisticsData.getIcLogLines().forEach { printer.println(it) }
            }
        }

        if (printMetrics) {
            printMetrics(
                statisticsData.getBuildTimesMetrics(), statisticsData.getPerformanceMetrics(), statisticsData.getNonIncrementalAttributes(),
                statisticsData.getGcTimeMetrics(), statisticsData.getGcCountMetrics(), printer = printer
            )
            printCustomTaskMetrics(statisticsData, printer)
        }
    }
}