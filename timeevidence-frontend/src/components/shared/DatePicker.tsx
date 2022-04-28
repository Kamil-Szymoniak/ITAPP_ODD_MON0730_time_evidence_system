import React from 'react';
import LocalizationProvider from "@mui/lab/LocalizationProvider";
import AdapterDateFns from "@mui/lab/AdapterDateFns";
import DatePicker from "@mui/lab/DatePicker";
import {TextField as MuiTextField} from "@mui/material";

type Props = {
    value: Date | null;
    onChange:  (date: (Date | null), selectionState?: string) => void //TDate instead of null check type/create interface
    minDate?: any;
    maxDate?: any;
    label: string;
    error?: boolean;
    helperText?: React.ReactNode;
    onBlur?: ((event: any ) => void);
    disabled?: boolean;
    style?: any;
}

export function CustomDatePicker(props: Props) {
    return (
        <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
                disabled={props.disabled}
                label={props.label}
                inputFormat="dd.MM.yyyy"
                mask="__.__.____"
                onChange={props.onChange}
                value={props.value}
                minDate={props.minDate}
                maxDate={props.maxDate}
                renderInput={(params) =>
                    <MuiTextField
                        margin={"normal"}
                        {...params}
                        error={props.error}
                        helperText={props.error ? props.helperText : ''}
                        onBlur={props.onBlur}
                    />}
            />
        </LocalizationProvider>
    );
}