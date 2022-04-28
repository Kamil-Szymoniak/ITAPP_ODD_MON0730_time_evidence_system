import React from "react";
import AppTableBody, {Column, Menu} from "./AppTableBody";
import {usePaging} from "./usePaging";
import {useSorting} from "./useSorting";
import {DATE_API_FORMAT, useFiltering} from "./useFiltering";
import {PageResponse} from "../../../dto/dto";
import {apiRequest, useApiRequest} from '../../../util/apiRequest'
import FilterAltIcon from '@mui/icons-material/FilterAlt';
import AdapterDateFns from "@mui/lab/AdapterDateFns";
import {
    IconButton,
    makeStyles, Paper,
    Table,
    TableContainer,
    TableHead,
    TableRow,
    TextField,
    Toolbar,
} from "@material-ui/core";
import {MenuItem, Select, TableFooter, TablePagination, TableCell, TableSortLabel, TextField as MuiTextField} from "@mui/material";
import {DatePicker, LocalizationProvider} from "@mui/lab";
import dayjs from "dayjs";


export const booleanFilter = [
    {key: 'Yes', name: 'true'},
    {key: 'No', name: 'false'},
    {key: 'Both', name: ''}
];

type Props<T> = {
    title?: string;
    description?: string;
    columns: Column<T>[];
    url: string;
    actions?: Menu<T>[];
    onClick?: (row: T, index: number) => void;
    reload?: number;
    query?: Record<string, string | number | undefined>;
    toolbar?: React.ReactNode;
    pdfUrl?: string;
    constantQueries?: string;
}

export function createQuery(values: Record<string, string>, constantQueries?: string): string | undefined {
    const query = Object.keys(values)
        .filter((key) => values[key] !== '' && values[key] != null)
        .map((key) => `${key.replace("_", ".")}:${values[key]}`).join(',')
        + (constantQueries ?? '')

    return query !== '' ? query : undefined
}

const isBlank = (text: string | undefined | null) => text == null || text === ''

function DataTable<T>(props: Props<T>) {
    const classes = useStyles()
    const paging = usePaging()
    const sorting = useSorting()
    const filtering = useFiltering()
    const request = useApiRequest<PageResponse<T>>(() => apiRequest({
            method: 'GET',
            path: props.url,
            query: {
                pageNumber: paging.pageNumber,
                pageSize: paging.pageSize,
                sortBy: sorting.sortBy,
                sortOrder: sorting.sortOrder,
                search: createQuery(filtering.filters, props.constantQueries),
                ...props.query,
            },
            minDelay: 150,
        }),
        [paging.pageNumber, paging.pageSize, sorting.sortBy, sorting.sortOrder, filtering.filters, props.url, props.reload,
            JSON.stringify(props.query ?? {})])
    return (
        <TableContainer component={Paper}>
            <Toolbar>
                {props.toolbar}
                <IconButton onClick={filtering.onFiltersEnabledChange}><FilterAltIcon/>
                </IconButton>
            </Toolbar>
            <Table className={classes.table}
                   size="medium"
            >
                <TableHead>
                    <TableRow key={'sorting'} className={classes.tableHead}>
                        {props.actions && (
                            <TableCell key={'actions sorting'} width="7">Actions</TableCell>
                        )}
                        {props.columns.map((column) => (
                            <TableCell key={`${column.name} sorting`}>
                                <TableSortLabel
                                    key={column.field}
                                    direction={sorting.sortOrder}
                                    onClick={() => sorting.onSortChange(column.field)}
                                    hideSortIcon={column.sort === false}
                                    active={column.sort === true && sorting.sortBy === column.field}
                                >
                                    {column.name}
                                </TableSortLabel>
                            </TableCell>
                        ))}

                    </TableRow>
                    {filtering.filtersEnabled && (
                        <TableRow key={'filtering'} className={classes.tableHead}>
                            {props.actions && (
                                <TableCell key={'actions filtering'} style={{marginLeft: 7}}/>
                            )}
                            {props.columns.map((column) => (
                                <TableCell
                                    key={`${column.field} name`}
                                    className={classes.filterCellStyles}
                                >
                                    {(column.filter && (column.type === undefined || column.type === 'text')) && (
                                        <TextField
                                            variant="outlined"
                                            size="small"
                                            label=""
                                            id={column.field.replace(".", "_")}
                                            onChange={filtering.onFilterChange}
                                            onBlur={filtering.onFilterBlur}
                                            value={filtering.filterInputs[column.field.replace(".", "_")] ?? ''}
                                        />
                                    )}
                                    {(column.filter && column.type === 'boolean') && (
                                        <Select
                                            style={{minWidth: 60}}
                                            size="small"
                                            variant="outlined"
                                            id={column.field}
                                            onChange={filtering.onSelectFilterChange(column.field.replace(".", "_"))}
                                            onBlur={filtering.onFilterBlur}
                                            value={filtering.filterInputs[column.field.replace(".", "_")]}
                                        >{booleanFilter.map((b) => (
                                            <MenuItem value={b.name}>
                                                {b.key}
                                            </MenuItem>
                                        ))}
                                        </Select>
                                    )}
                                    {(column.filter && (column.type === 'date' || column.type === 'dateTime')) && (
                                        <LocalizationProvider dateAdapter={AdapterDateFns}>
                                            <DatePicker
                                                inputFormat="dd.MM.yyyy"
                                                mask="__.__.____"
                                                onChange={filtering.onDateFilterChange((column.field.replace(".", "_")))}
                                                value={isBlank(filtering.filterInputs[(column.field.replace(".", "_"))]) ? null : dayjs(filtering.filterInputs[(column.field.replace(".", "_"))], DATE_API_FORMAT).toDate()}
                                                renderInput={(params) =>
                                                    <MuiTextField
                                                        size="small"
                                                        label=""
                                                        style={{minWidth: 150, paddingBottom: 5, paddingTop: 5}}
                                                        onBlur={filtering.onFilterBlur}
                                                        {...params}
                                                    />}
                                            />
                                        </LocalizationProvider>
                                    )}
                                </TableCell>
                            ))}
                        </TableRow>
                    )}
                </TableHead>
                <AppTableBody
                    error={request.error}
                    columns={props.columns}
                    data={request.data}
                    isLoading={request.isLoading}
                    pageSize={paging.pageSize}
                    reload={request.reload}
                    actions={props.actions}
                    onClick={props.onClick}
                />
                <TableFooter>
                    <tr>
                        <TablePagination
                            count={request.data?.totalElements ?? 0}
                            page={paging.pageNumber}
                            onPageChange={paging.handleChangePage}
                            rowsPerPageOptions={[5, 10, 20, {value: -1, label: 'All'}]}
                            rowsPerPage={paging.pageSize}
                            onRowsPerPageChange={paging.handleChangeRowsPerPage}
                        />
                    </tr>
                </TableFooter>
            </Table>

        </TableContainer>
    );
}

export default DataTable;

const useStyles = makeStyles({
    table: {
        borderColor: '#000000',
        border: "1px",
    },
    tableHead: {
        background: '#FF69B4',
    },
    filterCellStyles: {
        paddingBottom: "5px!important",
        paddingTop: "5px!important",
    }
});