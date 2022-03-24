import React, {ChangeEvent, useState} from 'react';

export function usePaging() {
    const [pageNumber, setPageNumber] = useState<number>(0)
    const [pageSize, setPageSize] = useState<number>(10)

    const handleChangePage = (
        event: React.MouseEvent<HTMLButtonElement> | null,
        newPage: number,
    ) => {
        setPageNumber(newPage)
    }

    const handleChangeRowsPerPage = (
        event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
    ) => {
        setPageSize(parseInt(event.target.value, 10))
        setPageNumber(0)
    }

    return {
        pageSize,
        pageNumber,
        handleChangePage,
        handleChangeRowsPerPage,
    }
}
