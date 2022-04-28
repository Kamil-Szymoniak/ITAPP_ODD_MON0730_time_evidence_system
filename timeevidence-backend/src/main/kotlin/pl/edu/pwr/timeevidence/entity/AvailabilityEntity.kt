package pl.edu.pwr.timeevidence.entity

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "availability", schema = "time")
class AvailabilityEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "availability_generator")
    @SequenceGenerator(
        name = "availability_generator",
        sequenceName = "availability_sequence",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    var id: Int? = null,
    @Column(name = "comment", nullable = true)
    var comment: String? = null,
    @Column(name = "date", nullable = false)
    var date: LocalDate,
    @Column(name="periods", nullable = false)
    var periods: String = "",
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_person", referencedColumnName = "id")
    var person: PersonEntity? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_team", referencedColumnName = "id")
    var team: TeamEntity? = null,

) {
    constructor() : this(date = LocalDate.now())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AvailabilityEntity

        if (id != other.id) return false
        if (comment != other.comment) return false
        if (date != other.date) return false
        if (person != other.person) return false
        if (team != other.team) return false
        if (periods != other.periods) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (comment?.hashCode() ?: 0)
        result = 31 * result + date.hashCode()
        result = 31 * result + (person?.hashCode() ?: 0)
        result = 31 * result + (team?.hashCode() ?: 0)
        result = 31 * result + periods.hashCode()
        return result
    }

    override fun toString(): String {
        return "AvailabilityEntity(id=$id, comment=$comment, date=$date, periods='$periods', person=${person?.id}, team=${team?.id})"
    }

}