package pl.edu.pwr.timeevidence.specification.entity

import pl.edu.pwr.timeevidence.entity.TeamEntity
import pl.edu.pwr.timeevidence.specification.FieldInfo
import pl.edu.pwr.timeevidence.specification.MappedSpecification

class TimeEvidenceSpecification : MappedSpecification<TeamEntity>() {
    override fun getFieldMap(): Map<String, FieldInfo> {
        return fieldMap(
            field("id", "id"),
            field("date", "date"),
            field("minutes", "minutes"),
            field("comment", "comment"),
            field("status", "status"),
            field("person.id", "person", "id"),
            field("project.id", "project", "id")
        )
    }
}