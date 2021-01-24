package ge.mov.mobile.data.model.Series

data class Person (
    val data: List<PersonModel>
)

data class PersonModel (
    val id: Long,
    val originalName: String,
    val primaryName: String,
    val secondaryName: String,
   // val tertiaryName: String,
    val poster: String,
 //   val birthDate: String,
 //   val slogan: String,
 //   val zodiacSign: String,
    val personRole: PersonRole
)

data class PersonRole (
    val data: Role
)

data class Role (
    val role: String,
    val character: String
)