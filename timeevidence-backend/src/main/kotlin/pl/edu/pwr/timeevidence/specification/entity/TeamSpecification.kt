package pl.edu.pwr.timeevidence.specification.entity

import pl.edu.pwr.timeevidence.entity.TeamEntity
import pl.edu.pwr.timeevidence.specification.FieldInfo
import pl.edu.pwr.timeevidence.specification.MappedSpecification

class TeamSpecification : MappedSpecification<TeamEntity>() {
    override fun getFieldMap(): Map<String, FieldInfo> {
        return fieldMap(
            field("id", "id"),
            field("name", "name"),
            field("description", "description"),
            field("teamMembers", "teamMembers").isSet("id"),
            field("teamLeader.name", "teamLeader", "name")
        )
    }
}