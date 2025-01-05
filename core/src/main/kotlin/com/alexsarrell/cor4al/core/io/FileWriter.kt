package com.alexsarrell.cor4al.core.io

import com.alexsarrell.cor4al.core.configuration.FileWriterConfiguration
import com.alexsarrell.cor4al.core.pipeline.context.GenerationContext
import com.alexsarrell.cor4al.core.pipeline.context.outputDir
import com.alexsarrell.cor4al.core.pipeline.context.packageName
import java.io.File

class FileWriter(
    private val configuration: FileWriterConfiguration,
    private val fileName: String,
) : Writer {
    override fun writeResult(
        code: String,
        context: GenerationContext,
    ) {
        File(buildPath(fileName, context)).apply { parentFile?.mkdirs() }.writeText(code)
    }

    private fun buildPath(
        fileName: String,
        context: GenerationContext,
    ): String =
        context.outputDir!! +
            configuration.sourceDir + "/" +
            context.packageName!!.split(".").joinToString("/") +
            "/$fileName"
}
