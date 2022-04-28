package pl.edu.pwr.timeevidence.specification.entity

import pl.edu.pwr.timeevidence.specification.FieldInfo
import pl.edu.pwr.timeevidence.specification.MappedSpecification

class AvailabilitySpecification : MappedSpecification<AvailabilitySpecification>() {
    override fun getFieldMap(): Map<String, FieldInfo> {
        return fieldMap(
            field("id", "id"),
            field("comment", "comment"),
            field("date", "date"),
            field("person.id", "person", "id"),
            field("team.id", "team", "id")
        )
    }
}