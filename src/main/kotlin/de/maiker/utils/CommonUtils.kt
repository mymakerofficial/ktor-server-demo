package de.maiker.utils

fun String?.getIfNotEmpty() = if (this.isNullOrEmpty()) null else this

fun String?.getIfNotEmptyElse(other: String) = if (this.isNullOrEmpty()) other else this