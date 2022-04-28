import {BasicResponse, DictionaryResponse, PersonRequest, PersonResponse} from "../dto/dto";
import {apiRequest, basicRequest} from "../util/apiRequest";

export function addPerson(values: PersonRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/persons`,
        method: 'POST',
        body: JSON.stringify(values),

    });
}

export function editPerson(personId: number, personData: PersonRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/persons/${personId}`,
        method: 'PUT',
        body: JSON.stringify(personData),
    });
}

export function deletePerson(personId: number) {
    return basicRequest<BasicResponse>({
        url: `/api/persons/${personId}`,
        method: 'DELETE',
    });
}

export function getPerson(personId: number) {
    return apiRequest<PersonResponse>({
        method: 'GET',
        path: `/api/persons/${personId}`,
    });
}

export function getAllPersons() {
    return apiRequest<DictionaryResponse[]>({
        method: 'GET',
        path: `/api/persons/all`,
    });
}