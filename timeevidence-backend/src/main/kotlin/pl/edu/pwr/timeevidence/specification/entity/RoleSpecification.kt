package pl.edu.pwr.timeevidence.specification.entity

import pl.edu.pwr.timeevidence.entity.RoleEntity
import pl.edu.pwr.timeevidence.specification.FieldInfo
import pl.edu.pwr.timeevidence.specification.MappedSpecification

class RoleSpecification : MappedSpecification<RoleEntity>() {
    override fun getFieldMap(): Map<String, FieldInfo> {
        return fieldMap(
            field("id", "id"),
            field("name", "name"),
            field("description", "description"),
            field("permissions", "permissions").isSet("id"),
            field("permissions.name", "permissions", "name")
        )
    }
}