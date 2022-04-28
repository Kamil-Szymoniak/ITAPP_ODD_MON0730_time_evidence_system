package pl.edu.pwr.timeevidence.specification.entity

import pl.edu.pwr.timeevidence.entity.PermissionEntity
import pl.edu.pwr.timeevidence.specification.FieldInfo
import pl.edu.pwr.timeevidence.specification.MappedSpecification

class PermissionSpecification : MappedSpecification<PermissionEntity>() {
    override fun getFieldMap(): Map<String, FieldInfo> {
        return fieldMap(
            field("id", "id"),
            field("name", "name"),
            field("description", "description")
        )
    }
}