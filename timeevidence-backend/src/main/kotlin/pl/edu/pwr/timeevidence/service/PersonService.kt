package pl.edu.pwr.timeevidence.service

import org.springframework.stereotype.Service
import pl.edu.pwr.timeevidence.dao.PersonRepository
import pl.edu.pwr.timeevidence.dto.DictionaryResponse
import pl.edu.pwr.timeevidence.dto.PersonRequest
import pl.edu.pwr.timeevidence.dto.PersonResponse
import pl.edu.pwr.timeevidence.entity.PersonEntity
import pl.edu.pwr.timeevidence.exception.NotFoundException

@Service
class PersonService (private val personRepository: PersonRepository) {
    fun createPerson(request: PersonRequest) = personRepository.save(fromDto(request)).id!!
    fun editPerson(request: PersonRequest, id: Int) =
        if (!personRepository.existsById(id)) throw NotFoundException("Person", "id", id)
        else PersonResponse.fromEntity(personRepository.save(fromDto(request, id)))
    fun getPerson(id: Int) = PersonResponse.fromEntity(personRepository.findById(id)
        .orElseThrow { throw NotFoundException("Person", "id", id) })
    //fun getPersons(criteria: EntityCriteria<PersonEntity>) =
    //    PagedResponse(personRepository.findAll(criteria.specification, criteria.paging!!).map { PersonResponse.fromEntity(it) })
    fun getAllPersons() = personRepository.findAll().map { DictionaryResponse.fromPerson(it) }.toList()
    fun deletePerson(id: Int) {
        if (personRepository.findById(id).isEmpty) {
            throw NotFoundException("Person", "id", id)
        }
        personRepository.deleteById(id)
    }

    fun fromDto(request: PersonRequest) = PersonEntity(
        name = request.name,
        surname = request.surname,
        phone = request.phone,
        birthday = request.birthday
    )

    fun fromDto(request: PersonRequest, id: Int) = PersonEntity(
        id = id,
        name = request.name,
        surname = request.surname,
        phone = request.phone,
        birthday = request.birthday
    )
}