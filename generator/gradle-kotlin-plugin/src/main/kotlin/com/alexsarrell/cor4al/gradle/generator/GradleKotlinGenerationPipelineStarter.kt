package com.alexsarrell.cor4al.gradle.generator

import com.alexsarrell.cor4al.gradle.api.pipeline.pipe.GeneratePipe
import com.alexsarrell.cor4al.gradle.api.GradleGenerationPipelineStarter
import com.alexsarrell.cor4al.gradle.api.pipeline.pipe.ParsePipe
import com.alexsarrell.cor4al.core.pipeline.pipe.context.ParsePipeContext
import com.alexsarrell.cor4al.core.generator.BasicGenerator
import com.alexsarrell.cor4al.core.parser.BasicYamlParser
import com.alexsarrell.cor4al.core.pipeline.*
import com.alexsarrell.cor4al.core.pipeline.context.*
import com.alexsarrell.cor4al.generator.kotlin.JavaTypeMappings
import com.alexsarrell.cor4al.gradle.api.GenerationMode
import com.alexsarrell.cor4al.gradle.api.pipeline.pipe.LoadPipe
import com.alexsarrell.cor4al.gradle.api.pipeline.pipe.ValidationPipe
import com.alexsarrell.cor4al.gradle.api.tasks.Cor4alGenerateTask

class GradleKotlinGenerationPipelineStarter : GradleGenerationPipelineStarter {
    private val pipeline: GenerationPipeline = BasicGenerationPipeline()
    private val context: GenerationContext = pipeline.context
    private val parser: BasicYamlParser = BasicYamlParser(ParsePipeContext(context))
    private val generator: BasicGenerator = BasicGenerator()

    override fun runPipeline(task: Cor4alGenerateTask) {
        fillContextStartParams(task)

        pipeline.generate {
            process(LoadPipe(context, task))
            process(ParsePipe(parser, task))
            process(ValidationPipe()) { task.mode.get().equals(GenerationMode.SAFE.name, ignoreCase = true) }
            process(GeneratePipe(context, generator))
        }
    }

    private fun fillContextStartParams(task: Cor4alGenerateTask) {
        context.packageName = task.packageName.get()
        context.parentPackage = task.parentPackage.getOrElse(task.packageName.get())
        context.outputDir = task.outputDir.get()
        context.templateDir = task.templateDir.orNull
        context.generatorFileExtension = "kt"
        context.typeMappings = JavaTypeMappings.getMappings()
    }
}