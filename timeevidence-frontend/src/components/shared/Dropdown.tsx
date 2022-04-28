import {FormControl, FormHelperText, InputLabel, MenuItem, Select} from "@mui/material";
import React from "react";
import {DictionaryResponse} from "../../dto/dto";

const ITEM_HEIGHT = 100;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
    PaperProps: {
        style: {
            maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
            maxWidth: 900,
        },
    },
};

type Props = {
    id: string
    value: string | null;
    onChange: (event: object) => void
    label?: string;
    error?: boolean;
    placeholder?: string;
    helperText?: React.ReactNode;
    onBlur?: ((event: any) => void);
    multiple?: boolean;
    dropdownData: any;
    disabled?: boolean;
}

export function Dropdown(props: Props) {

    return (
        <FormControl margin="normal">
            <InputLabel error={props.error}>{props.label}</InputLabel>
            <Select
                style={props.multiple ? {maxWidth: '900px'} :  undefined}
                key={props.multiple ? undefined : props.value}
                label={props.label}
                id={props.id}
                multiple={props.multiple}
                variant="outlined"
                placeholder={props.placeholder}
                onChange={props.onChange}
                onBlur={props.onBlur}
                value={props.value}
                error={props.error}
                disabled={props.disabled}
                MenuProps={MenuProps}
            >
                {props.dropdownData ? props.dropdownData.map((data: DictionaryResponse) => (
                    <MenuItem value={data.id} key={data.id}>
                        {data.name}
                    </MenuItem>
                )) : (
                    <MenuItem value={''} key={'empty'}>
                        {''}
                    </MenuItem>
                )}
            </Select>
            {props.error ?
                <FormHelperText
                    error={props.error}
                >
                    {props.helperText}
                </FormHelperText> : ''
            }
        </FormControl>

    );
}

type EnumProps = {
    id: string
    value: string;
    onChange: (event: object) => void
    dropdownData: string[];
    label?: string;
    error?: boolean;
    helperText?: React.ReactNode;
    onBlur?: ((event: any) => void);
    disabled?: boolean;
}

export const EnumDropdown = (props: EnumProps) => {
    return (
        <div>
            <FormControl margin={"normal"}>
                <InputLabel error={props.error}>{props.label}</InputLabel>
                <Select
                    id={props.id}
                    variant="outlined"
                    label={props.label}
                    value={props.value}
                    onChange={props.onChange}
                    onBlur={props.onBlur}
                    MenuProps={MenuProps}
                    disabled={props.disabled}
                >
                    {props.dropdownData.map((data) => (
                        <MenuItem
                            key={data}
                            value={data}
                        >
                            {data}
                        </MenuItem>
                    ))}
                </Select>
                {props.error ?
                    <FormHelperText
                        error={props.error}
                    >
                        {props.helperText}
                    </FormHelperText> : ''
                }
            </FormControl>
        </div>
    )
}
