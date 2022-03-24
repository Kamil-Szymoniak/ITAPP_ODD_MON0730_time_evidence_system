import { useState } from 'react';

type SortState = null | {
    field: string,
    direction: 'asc' | 'desc'
}

export function useSorting() {
    const [sort, setSort] = useState<SortState>(null)

    const onSortChange = (field: string) => {
        let direction: 'asc' | 'desc' = 'asc'

        if (sort?.field === field && sort?.direction === 'asc') {
            direction = 'desc'
        }
        if (sort?.field === field && sort?.direction === 'desc') {
            setSort(null)
            return
        }

        setSort({
            field,
            direction,
        })
    }

    return {
        sortBy: sort?.field,
        sortOrder: sort?.direction,
        onSortChange,
    }
}
