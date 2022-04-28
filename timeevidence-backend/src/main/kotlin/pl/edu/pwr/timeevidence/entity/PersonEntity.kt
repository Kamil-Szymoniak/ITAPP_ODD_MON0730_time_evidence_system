package pl.edu.pwr.timeevidence.entity

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "person", schema = "ppl")
class PersonEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "person_generator")
    @SequenceGenerator(
        name = "person_generator",
        sequenceName = "person_sequence",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    var id: Int? = null,
    @Column(name = "name", nullable = false)
    var name: String,
    @Column(name = "surname", nullable = false)
    var surname: String,
    @Column(name = "phone", nullable = true)
    var phone: String?,
    @Column(name = "birthday", nullable = false)
    var birthday: LocalDate,
    @OneToOne(mappedBy = "person")
    var user: UserEntity? = null,
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "projectMembers")
    var projects: List<ProjectEntity> = emptyList(),
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "projectManager")
    var managedProjects: List<ProjectEntity> = emptyList(),
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "teamMembers")
    var teams: List<TeamEntity> = emptyList(),
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "teamLeader")
    var ledTeams: List<TeamEntity> = emptyList()
) {
    constructor() : this(null, "", "", "", LocalDate.now())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (surname != other.surname) return false
        if (phone != other.phone) return false
        if (birthday != other.birthday) return false
        if (user != other.user) return false
        if (projects != other.projects) return false
        if (managedProjects != other.managedProjects) return false
        if (teams != other.teams) return false
        if (ledTeams != other.ledTeams) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + surname.hashCode()
        result = 31 * result + (phone?.hashCode() ?: 0)
        result = 31 * result + birthday.hashCode()
        result = 31 * result + (user?.hashCode() ?: 0)
        result = 31 * result + projects.hashCode()
        result = 31 * result + managedProjects.hashCode()
        result = 31 * result + teams.hashCode()
        result = 31 * result + ledTeams.hashCode()
        return result
    }

    override fun toString(): String {
        return "PersonEntity(id=$id, name='$name', surname='$surname', phone=$phone, birthday=$birthday, user=${user?.id}," +
                " projects=${projects.map { it.id }}, managedProjects=${managedProjects.map { it.id }}," +
                " teams=${teams.map { it.id }}, ledTeams=${ledTeams.map { it.id }})"
    }


}