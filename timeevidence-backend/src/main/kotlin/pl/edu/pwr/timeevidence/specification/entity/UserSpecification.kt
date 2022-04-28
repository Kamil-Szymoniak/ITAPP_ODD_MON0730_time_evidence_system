package pl.edu.pwr.timeevidence.specification.entity

import pl.edu.pwr.timeevidence.entity.UserEntity
import pl.edu.pwr.timeevidence.specification.FieldInfo
import pl.edu.pwr.timeevidence.specification.MappedSpecification

class UserSpecification : MappedSpecification<UserEntity>() {
    override fun getFieldMap(): Map<String, FieldInfo> {
        return fieldMap(
            field("id", "id"),
            field("username", "username"),
            field("email", "email"),
            field("person.id", "person", "id"),
            field("roles", "roles").isSet("id")
        )
    }
}