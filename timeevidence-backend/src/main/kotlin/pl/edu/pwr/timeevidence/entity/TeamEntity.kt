package pl.edu.pwr.timeevidence.entity

import javax.persistence.*

@Entity
@Table(name = "team", schema = "ppl")
class TeamEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "team_generator")
    @SequenceGenerator(
        name = "team_generator",
        sequenceName = "team_sequence",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    var id: Int? = null,
    @Column(name = "name", nullable = false)
    var name: String,
    @Column(name = "description", nullable = true)
    var description: String? = null,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "many_team_has_many_person",
        schema = "ppl",
        joinColumns = [JoinColumn(name = "id_team", referencedColumnName = "id", nullable = false)],
        inverseJoinColumns = [JoinColumn(name = "id_person", referencedColumnName = "id", nullable = false)]
    )
    var teamMembers: List<PersonEntity> = emptyList(),
    @ManyToOne
    @JoinColumn(name = "id_person", referencedColumnName = "id")
    var teamLeader: PersonEntity? = null,
) {
    constructor() : this(name = "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TeamEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (teamMembers != other.teamMembers) return false
        if (teamLeader != other.teamLeader) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + teamMembers.hashCode()
        result = 31 * result + (teamLeader?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "TeamEntity(id=$id, name='$name', description=$description, " +
                "teamMembers=${teamMembers.map { it.id }}, teamLeader=${teamLeader?.id})"
    }


}