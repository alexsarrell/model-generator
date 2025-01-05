package com.alexsarrell.cor4al.core.generator

import com.alexsarrell.cor4al.core.configuration.FileWriterConfiguration
import com.alexsarrell.cor4al.core.io.FileWriter
import com.alexsarrell.cor4al.core.model.ModelAccessor
import com.alexsarrell.cor4al.core.pipeline.context.GenerationContext
import com.alexsarrell.cor4al.core.pipeline.context.generatorFileExtension
import com.alexsarrell.cor4al.core.pipeline.pipe.context.ClassSchema
import com.alexsarrell.cor4al.core.pipeline.pipe.context.ParsePipeContext
import com.alexsarrell.cor4al.core.pipeline.context.templateDir
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.github.jknack.handlebars.io.FileTemplateLoader

class BasicGenerator : CodeGenerator {
    private val writerConfiguration = FileWriterConfiguration("/src/main/kotlin")

    private val handlebars =
        Handlebars()
            .apply {
                with(ClassPathTemplateLoader("/templates", ".hbs"))
            }

    override fun generate(context: ParsePipeContext) {
        if (context.context.templateDir != null) {
            handlebars.with(FileTemplateLoader(context.context.templateDir))
        }

        context.specs.forEach { spec ->
            spec.schemas.forEach {
                generateSchema(it.key to it.value, context.context)
            }
        }
    }

    private fun generateSchema(
        schema: ClassSchema,
        context: GenerationContext,
    ) {
        val compiled =
            handlebars.compile(
                schema.second.type.name
                    .lowercase(),
            )

        val modelAccessor =
            ModelAccessor(schema, context)

        FileWriter(
            writerConfiguration,
            "${modelAccessor.className}.${context.generatorFileExtension}",
        ).writeResult(
            compiled.apply(modelAccessor),
            context,
        )
    }
}
