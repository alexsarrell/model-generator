package com.alexsarrell.arch.core.generator

import com.alexsarrell.arch.core.generator.helper.IsEmptyHelper
import com.alexsarrell.arch.core.model.Spec
import com.alexsarrell.arch.core.model.accessor.ModelAccessor
import com.alexsarrell.arch.core.pipeline.context.PipelineContext
import com.alexsarrell.arch.core.pipeline.context.generatorFileExtension
import com.alexsarrell.arch.core.pipeline.context.outputDir
import com.alexsarrell.arch.core.pipeline.context.packageName
import com.alexsarrell.arch.core.pipeline.context.sourceDir
import com.alexsarrell.arch.core.pipeline.pipe.context.ClassSchema
import com.alexsarrell.arch.core.util.build
import com.alexsarrell.arch.core.util.writeToFile
import com.github.jknack.handlebars.io.FileTemplateLoader
import java.io.File

class BasicGenerator(templateDir: String?) : AbstractGenerator("/templates") {
    init {
        if (templateDir != null) {
            handlebars.with(FileTemplateLoader(templateDir))
        }
        handlebars.registerHelper("isEmpty", IsEmptyHelper())
    }

    override fun PipelineContext.generateSpec(spec: Spec) {
        spec.schemas.forEach {
            generateSchema(it.key to it.value)
        }
    }

    private fun PipelineContext.generateSchema(schema: ClassSchema) {
        val template = handlebars.compile(schema.second.type.name.lowercase())
        val modelAccessor = ModelAccessor(schema, this)
        val file = File(buildPath(modelAccessor)).build()

        template.writeToFile(modelAccessor, file)
    }

    private fun PipelineContext.buildPath(modelAccessor: ModelAccessor): String =
        outputDir.split("/", "\\")
            .plus(sourceDir.split("/", "\\"))
            .plus(packageName.split("."))
            .plus("${modelAccessor.className}.${generatorFileExtension}")
            .joinToString(File.separator)
}
