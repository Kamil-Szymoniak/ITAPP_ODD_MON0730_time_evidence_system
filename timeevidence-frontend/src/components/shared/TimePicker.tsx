import React from 'react';
import LocalizationProvider from "@mui/lab/LocalizationProvider";
import AdapterDateFns from "@mui/lab/AdapterDateFns";
import {TextField as MuiTextField} from "@mui/material";
import {TimePicker} from "@mui/lab";
import frLocale from 'date-fns/locale/fr';

type Props = {
    value: Date | null;
    onChange:  (date: (Date | null), selectionState?: string) => void //TDate instead of null check type/create interface
    label: string;
    error?: boolean;
    helperText?: React.ReactNode;
    onBlur?: ((event: any ) => void);
    disabled?: boolean;
    style?: any;
}

export function CustomTimePicker(props: Props) {
  return (
    
        <LocalizationProvider dateAdapter={AdapterDateFns} locale={frLocale}>
            <TimePicker
                disabled={props.disabled}
                label={props.label}
                inputFormat="HH:mm"
                mask="__:__"
                onChange={props.onChange}
                value={props.value}
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