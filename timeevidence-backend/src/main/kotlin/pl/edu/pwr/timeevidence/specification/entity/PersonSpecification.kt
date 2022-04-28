package pl.edu.pwr.timeevidence.specification.entity

import pl.edu.pwr.timeevidence.entity.PersonEntity
import pl.edu.pwr.timeevidence.specification.FieldInfo
import pl.edu.pwr.timeevidence.specification.MappedSpecification

class PersonSpecification : MappedSpecification<PersonEntity>() {
    override fun getFieldMap(): Map<String, FieldInfo> {
        return fieldMap(
            field("id", "id"),
            field("name", "name"),
            field("surname", "surname"),
            field("phone", "phone"),
            field("birthday", "birthday"),
            field("user.id", "user", "id"),
            field("projects.id", "projects").isSet("id")
        )
    }
}