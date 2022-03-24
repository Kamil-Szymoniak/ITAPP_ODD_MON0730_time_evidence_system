import {useFormik} from 'formik';
import {useEffect, useState} from 'react';
import dayjs from "dayjs";

export class CancellationToken {
    private shouldContinue: boolean = true

    cancel = () => { this.shouldContinue = false }

    continue= () => this.shouldContinue
}

export const DATE_API_FORMAT = 'YYYY-MM-DD'

export function delay(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(() => resolve(), ms))
}

export function useFiltering() {
    const formik = useFormik<Record<string, string>>({
        initialValues: {},
        onSubmit: () => {
        },
    })

    const [filters, setFilters] = useState<Record<string, string>>(formik.values)

    const [filtersEnabled, setFilterEnabled] = useState(false)

    useEffect(() => {
        const token = new CancellationToken()

        delay(300).then(() => {
            if (token.continue()) {
                setFilters(formik.values)
            }
        })

        return () => token.cancel()
    }, [formik.values])

    const onFiltersEnabledChange = () => {
        setFilterEnabled(!filtersEnabled)
    }

    const selectFilterChange = (id: string) => {
        return (e: any) => {
            formik.setFieldValue(id, e.target.value)
        }
    }

    const dateFilterChange = (id: string) => {
        return (e: any) => {
            formik.setFieldValue(id, e != null ? dayjs(e).format(DATE_API_FORMAT) : '')
        }
    }

    return {
        filters,
        filtersEnabled,
        onFiltersEnabledChange,
        onSelectFilterChange: selectFilterChange,
        onDateFilterChange: dateFilterChange,
        filterInputs: formik.values,
        onFilterChange: formik.handleChange,
        onFilterBlur: formik.handleBlur,
    }
}

