package com.alexsarrell.cor4al.core.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Version(val code: String)