import React from "react";
import {PageResponse} from "../../../dto/dto";
import {Button, makeStyles, MenuItem, TableBody, TableCell, TableRow, Tooltip} from "@material-ui/core";
import {Skeleton, Typography} from "@mui/material";
import CheckIcon from '@mui/icons-material/Check';
import CloseIcon from '@mui/icons-material/Close';
import {formatAmount, formatDate, formatDateTime, timeSince} from "../../../util/format";

export type Column<T> = {
    name: React.ReactNode,
    field: (keyof T & string) | string;
    type?: 'text' | 'date' | 'dateTime' | 'dateTimeRelative' | 'amountTime' | 'boolean';
    filter?: boolean;
    sort?: boolean;
    width?: string;
}
type MenuPrototype<T> = {
    key?: string,
    onClick: (data: T, index: number) => void;
    content: React.ReactNode;
    icon?: React.ReactNode;
    danger?: boolean;
    disable?: (data: T, index: number) => boolean
}

export type Menu<T> = MenuPrototype<T> | ((row: T) => MenuPrototype<T> | null)

const resolve = (obj: any, path: string): unknown => path.split('.').reduce((p, c) => p?.[c], obj)

type Props<T> = {
    columns: Column<T>[];
    actions?: Menu<T>[];
    data: Readonly<PageResponse<T>> | null;
    pageSize: number;
    onClick?: (row: T, index: number) => void;
    reload: () => void;
    isLoading: boolean;
    error: { message: string } | null;
}


function AppTableBody<T>(props: Props<T>) {
    const styles = useStyles();

    return (
        <TableBody>
            {props.error == null && !props.isLoading && props.data?.items.map((row, index) => (
                <TableRow key={index} className={styles.tableRow}>
                    {props.actions != null && (
                        <TableCell key={`${index} actions`} className={styles.tableCell}>
                            {props.actions
                                .map((menu) => {
                                    if (typeof menu === 'function') {
                                        return menu(row);
                                    }
                                    return menu;
                                })
                                .filter((menu) => menu != null)
                                .map((menu, menuIndex) => (
                                    <>

                                            <MenuItem
                                                key={menu!.key ?? `${menuIndex} 1`}
                                                className={menu!.icon != null ? 'has-icon' : 'no-icon'}
                                                onClick={() => menu!.onClick(row, index)}
                                                disabled={menu!.disable ? menu!.disable(row, index) : false}
                                            >
                                                {menu!.icon}
                                            </MenuItem>

                                    </>
                                ))}

                        </TableCell>
                    )}
                    {props.columns.map((column) => {
                        const value = resolve(row, column.field);
                        return (
                            <TableCell
                                key={`${column.field} ${index}`}
                                className={styles.tableCell}
                                style={{width: column.width}}
                                onClick={() => props.onClick?.(row, index)} //edit after click on some row
                            >
                                {column.type === 'date' && formatDate(value)}
                                {column.type === 'dateTime' && formatDateTime(value)}
                                {column.type === 'dateTimeRelative' && (
                                    <Tooltip title={formatDateTime(value)}>
                                        <p>{timeSince(value)}</p>
                                    </Tooltip>
                                )}
                                {(column.type === 'text' || column.type == null) && value}
                                {(column.type === 'amountTime') && formatAmount(value)}
                                {column.type === 'boolean' &&
                                    (value ?
                                            <CheckIcon fontSize="large" color="success"/>
                                            : <CloseIcon fontSize="large" color="error"/>
                                    )}
                            </TableCell>
                        );
                    })}

                </TableRow>
            ))}
            {props.isLoading && Array.from({length: props.pageSize}, (_, index) => (

                <TableRow key={index} className={styles.tableRow}>
                    {props.actions != null && (
                        <TableCell key={'loading actions'}> actions</TableCell>
                    )}
                    {props.columns.map((column) => (
                        <TableCell key={`${column.name} loading`} style={{width: column.width}}>
                            <Skeleton width="auto"/>
                        </TableCell>
                    ))}

                </TableRow>
            ))}
            {props.error && (
                <TableRow key={'error'} className={styles.tableRow}>
                    <TableCell key={'error cell'} colSpan={props.columns.length}>
                        <Typography>
                            {'Error occurred: ' + props.error.message}
                        </Typography>
                        <Button
                            variant={"outlined"}
                            onClick={props.reload}
                        >Error, reload
                        </Button>
                    </TableCell>
                </TableRow>
            )}
        </TableBody>
    )
}

export default React.memo(AppTableBody) as typeof AppTableBody;

const useStyles = makeStyles({
    tableRow: {
        height: 30
    },
    tableCell: {
        padding: "0px 16px"
    }
});