package ru.cib.kotlinspringai.dto

import kotlinx.serialization.Serializable

@Serializable
data class FileCreation(
    var place: String,
    var file_name: String,
    var file_extension: String,
    var content: String
)