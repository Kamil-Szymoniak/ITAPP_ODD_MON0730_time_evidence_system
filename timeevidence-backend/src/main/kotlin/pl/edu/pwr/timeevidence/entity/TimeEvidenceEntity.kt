package pl.edu.pwr.timeevidence.entity

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "time_evidence", schema = "time")
class TimeEvidenceEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "time_evidence_generator")
    @SequenceGenerator(
        name = "time_evidence_generator",
        sequenceName = "time_evidence_sequence",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    var id: Int? = null,
    @Column(name = "date", nullable = false)
    var date: LocalDate,
    @Column(name = "minutes", nullable = false)
    var minutes: Short,
    @Column(name = "comment", nullable = true)
    var comment: String? = null,
    @Column(name = "status", nullable = false)
    var status: String = Status.SENT.name,
    @Column(name="status_comment", nullable = true)
    var statusComment: String? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_person", referencedColumnName = "id")
    var person: PersonEntity? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_project", referencedColumnName = "id")
    var project: ProjectEntity? = null,
) {
    constructor() : this(date = LocalDate.now(), minutes = 0)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimeEvidenceEntity

        if (id != other.id) return false
        if (date != other.date) return false
        if (minutes != other.minutes) return false
        if (comment != other.comment) return false
        if (person != other.person) return false
        if (project != other.project) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + date.hashCode()
        result = 31 * result + minutes
        result = 31 * result + (comment?.hashCode() ?: 0)
        result = 31 * result + (person?.hashCode() ?: 0)
        result = 31 * result + (project?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "TimeEvidenceEntity(id=$id, date=$date, minutes=$minutes, comment=$comment, status='$status', " +
                "statusComment=$statusComment, person=${person?.id}, project=${project?.id})"
    }


}