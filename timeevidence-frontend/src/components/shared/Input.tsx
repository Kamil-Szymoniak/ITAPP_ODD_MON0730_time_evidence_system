import React from "react";
import {TextField} from "@material-ui/core";

type Props = {
    id?: string
    value?: string | null;
    onChange?: (event: object) => void;
    label: string;
    type?: React.InputHTMLAttributes<unknown>['type'];
    error?: boolean;
    defaultValue?: any;
    placeholder?: string;
    helperText?: React.ReactNode;
    onBlur?: ((event: any ) => void);
    fullWidth?: boolean;
    autoFocus?: boolean;
    multiline?: boolean;
    disabled?: boolean;
    minRows?: number;
    inputProps?: any
}

export function Input(props: Props) {

    return (
        <TextField
            id={props.id}
            margin="normal"
            variant="outlined"
            type={props.type}
            label={props.label}
            placeholder={props.placeholder}
            disabled={props.disabled}
            defaultValue={props.defaultValue}
            onChange={props.onChange}
            onBlur={props.onBlur}
            value={props.value}
            error={props.error}
            helperText={props.helperText}
            fullWidth={props.fullWidth}
            autoFocus={props.autoFocus}
            multiline={props.multiline}
            minRows={props.minRows}
            inputProps={props.inputProps}
        />
    );
}