import * as React from 'react';
import Button from '@mui/material/Button';
import {FC} from "react";
import {Close} from "@material-ui/icons";
import {makeStyles} from "@material-ui/core/styles";
import {
    Theme,
    DialogTitle,
    Dialog,
    IconButton,
    DialogActions,
    DialogContent,
    DialogContentText
} from "@material-ui/core";

type Props = {
    dialogTitle: string | undefined
    dialogContent: string;
    isDialogOpen: boolean;
    onConfirm: any;
    onHide: any;
    onCancel: any;
    confirmText: string | undefined;
    cancelText: string | undefined;
}

const ConfirmationDialog: FC<Props> = (props) => {
    const classes = useStyles();

    return (
        <div>
            <Dialog open={props.isDialogOpen} onClose={props.onHide}>
                <DialogTitle id={"responsive-dialog-title"}>
                    {props.dialogTitle ? props.dialogTitle : "Confirmation dialog"}
                    <IconButton aria-label={"close"} className={classes.closeButton} onClick={() => props.onHide()}>
                        <Close/>
                    </IconButton>
                </DialogTitle>
                <DialogContent dividers>
                    <DialogContentText id="alert-dialog-description">
                        {props.dialogContent}
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={props.onConfirm} autoFocus>{props.confirmText ? props.confirmText : "Yes"}</Button>
                    <Button type="submit" onClick={props.onCancel}>{props.cancelText ? props.cancelText : "No"}</Button>
                </DialogActions>
            </Dialog>
        </div>
    )
}

const useStyles = makeStyles((theme: Theme) => ({
        closeButton: {
            position: 'absolute',
            right: theme.spacing(1),
            top: theme.spacing(1),
            color: theme.palette.grey[500],
        },
    }
));

export default ConfirmationDialog;