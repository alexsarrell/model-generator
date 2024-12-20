package com.alexsarrell.cor4al.generator.kotlin

import com.alexsarrell.cor4al.core.SchemaPropertyValidator
import com.alexsarrell.cor4al.core.model.SchemaProperty
import com.alexsarrell.cor4al.generator.kotlin.exception.IncompatiblePropertyValueException
import javax.script.ScriptEngineManager

class KotlinSchemaPropertyValidator : SchemaPropertyValidator {

    override fun validate(schemaProperty: SchemaProperty) {
        val javaClass = Class.forName(schemaProperty.type)

        if (schemaProperty.defaultValue != null) {
            validateValue(schemaProperty.defaultValue!!, javaClass)
        }
    }

    private fun validateValue(value: String, propertyClass: Class<out Any>) {
        val engine = ScriptEngineManager().getEngineByExtension("kts")
        val valueClass = engine.eval(value)::class.java
        if (valueClass != propertyClass) {
            throw IncompatiblePropertyValueException(propertyClass, value, valueClass)
        }
    }
}
