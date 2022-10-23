package ca.unb.mobiledev.superduperfitnessapp.model

class Sound private constructor(
    private val id: String?,
    private val name: String?,
    val description: String?,
    val soundTitle: String?,
) {
    // Only need to include getters
    val title: String
        get() = "$id: $name"

    data class Builder(
        var id: String? = null,
        var name: String? = null,
        var description: String? = null,
        var soundTitle: String? = null
    ) {

        fun id(id: String) = apply { this.id = id }
        fun name(name: String) = apply { this.name = name }
        fun description(description: String) = apply { this.description = description }
        fun soundTitle(soundTitle: String) = apply { this.soundTitle = soundTitle }

        fun build() = Sound(id, name, description, soundTitle)
    }
}