import {Button} from "@material-ui/core";
import {useHistory} from "react-router-dom";
import React from "react";

type ReturnButtonProps = {
    autoFocus?: boolean;
    disabled?: boolean;
    returnPath: string;
    children?: any[] | any
}

export function ReturnButton(props: ReturnButtonProps) {
    const history = useHistory();
    return (
        <Button
            variant="outlined"
            style={{ color: '#ffffff', backgroundColor: '#808080'}}
            disabled={props.disabled}
            onClick={() => history.push(props.returnPath)}
        >
            {props.children ?? 'Return'}
        </Button>
    );
}

type SaveButtonProps = {
    disabled?: boolean;
    onClick?: (e: any) => void
}

export function SaveButton(props: SaveButtonProps){
    return (
        <Button
            autoFocus
            variant="outlined"
            style={{marginRight: 10, color: '#909090', backgroundColor: '#735364'}}
            type="submit"
            disabled={props.disabled}
            onClick={props.onClick}
        >
            Save
        </Button>
    )
}
