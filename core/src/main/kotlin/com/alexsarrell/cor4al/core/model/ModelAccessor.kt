package com.alexsarrell.cor4al.core.model

import com.alexsarrell.cor4al.core.model.ModelAccessor.ModelProperty
import com.alexsarrell.cor4al.core.pipeline.context.GenerationContext
import com.alexsarrell.cor4al.core.pipeline.context.packageName
import com.alexsarrell.cor4al.core.pipeline.context.parentPackage
import com.alexsarrell.cor4al.core.pipeline.pipe.context.ClassSchema
import com.alexsarrell.cor4al.core.pipeline.context.typeMappings

data class ModelAccessor(
    val className: String,
    val parent: String?,
    val parentProperties: List<ModelProperty>,
    val version: String,
    val packageName: String,
    val properties: List<ModelProperty>,
    val isOpen: Boolean,
    val parentPackage: String?,
    val imports: List<String>?,
    val metadata: List<MetadataEntry>?,
) {
    data class MetadataEntry(
        val key: String,
        val value: String,
    )

    data class ModelProperty(
        val name: String,
        val required: Boolean,
        val description: String?,
        val type: String,
        val version: String,
        val defaultValue: String? = null,
    )

    constructor(schema: ClassSchema, context: GenerationContext) :
        this(
            className = schema.first,
            parent = schema.second.parent?.className,
            version = schema.second.version,
            properties = schema.second.properties.toModelProperties(),
            parentProperties = schema.second.extractParentFields().toModelProperties(),
            packageName = context.packageName!!,
            isOpen =
                when (schema.second.type) {
                    SchemaType.ABSTRACT -> true
                    else -> false
                },
            parentPackage = context.parentPackage,
            imports = schema.second.imports(context),
            metadata = schema.second.metadata?.map { MetadataEntry(it.key, it.value) },
        )
}

private fun Schema.extractParentFields(): Map<String, SchemaProperty> {
    val fields = mutableMapOf<String, SchemaProperty>()

    fun getParentProperties(parent: Schema?) {
        if (parent == null) return
        fields += parent.properties
        getParentProperties(parent.parent?.resolvedRef)
    }

    getParentProperties(parent?.resolvedRef)
    return fields
}

private fun Map<String, SchemaProperty>.toModelProperties(): List<ModelProperty> =
    entries.map {
        ModelProperty(
            name = it.key,
            required = it.value.required,
            description = it.value.description,
            type = it.value.type,
            version = it.value.version,
            defaultValue = it.value.defaultValue,
        )
    }

private fun Schema.imports(context: GenerationContext): List<String> =
    properties.values.mapNotNull { context.typeMappings?.get(it.type) } +
        extractParentFields().values.mapNotNull { context.typeMappings?.get(it.type) }