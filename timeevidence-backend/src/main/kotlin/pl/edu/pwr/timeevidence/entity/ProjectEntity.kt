package pl.edu.pwr.timeevidence.entity

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "project", schema = "pro")
class ProjectEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "project_generator")
    @SequenceGenerator(
        name = "project_generator",
        sequenceName = "project_sequence",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    var id: Int? = null,
    @Column(name = "name", nullable = false)
    var name: String,
    @Column(name = "inhouse_name", nullable = true)
    var inhouseName: String? = null,
    @Column(name = "description", nullable = true)
    var description: String? = null,
    @Column(name = "client_name")
    var clientName: String,
    @Column(name = "beginning_date")
    var beginningDate: LocalDate = LocalDate.now(),
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "many_project_has_many_person",
        schema = "pro",
        joinColumns = [JoinColumn(name = "id_project", referencedColumnName = "id", nullable = false)],
        inverseJoinColumns = [JoinColumn(name = "id_person", referencedColumnName = "id", nullable = false)]
    )
    var projectMembers: List<PersonEntity> = emptyList(),
    @ManyToOne
    @JoinColumn(name = "id_person", referencedColumnName = "id")
    var projectManager: PersonEntity? = null,
) {
    constructor() : this(name = "", clientName = "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProjectEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (inhouseName != other.inhouseName) return false
        if (description != other.description) return false
        if (clientName != other.clientName) return false
        if (beginningDate != other.beginningDate) return false
        if (projectMembers != other.projectMembers) return false
        if (projectManager != other.projectManager) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + (inhouseName?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + clientName.hashCode()
        result = 31 * result + beginningDate.hashCode()
        result = 31 * result + projectMembers.hashCode()
        result = 31 * result + (projectManager?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "ProjectEntity(id=$id, name='$name', inhouseName=$inhouseName, description=$description," +
                " clientName='$clientName', beginningDate=$beginningDate, " +
                "projectMembers=${projectMembers.map { it.id }}, projectManager=${projectManager?.id})"
    }


}