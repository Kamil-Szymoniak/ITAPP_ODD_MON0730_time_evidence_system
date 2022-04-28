package pl.edu.pwr.timeevidence.specification.entity

import pl.edu.pwr.timeevidence.entity.ProjectEntity
import pl.edu.pwr.timeevidence.specification.FieldInfo
import pl.edu.pwr.timeevidence.specification.MappedSpecification

class ProjectSpecification : MappedSpecification<ProjectEntity>() {
    override fun getFieldMap(): Map<String, FieldInfo> {
        return fieldMap(
            field("id", "id"),
            field("name", "name"),
            field("inhouseName", "inhouseName"),
            field("description", "description"),
            field("clientName", "clientName"),
            field("beginningDate", "beginningDate"),
            field("projectMembers", "projectMembers").isSet("id"),
            field("projectManager.name", "projectManager", "name")
        )
    }
}