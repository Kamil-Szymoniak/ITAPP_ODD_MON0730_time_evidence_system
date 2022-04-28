import {FormikProps} from "formik";

export function inputProps<T>(props: FormikProps<T>, id: keyof T) {
    return {
        helperText: props.touched[id] && props.errors[id],
        error: props.touched[id] && props.errors[id] != null,
        value: props.values[id] ?? '',
        name: id,
        id,
        onBlur: props.handleBlur,
        onChange: props.handleChange,
    };
}
export function inputPropsFormik<T>(id: string, formikProps: FormikProps<T>) {
    return {
        id,
        value: getProperty(formikProps.values, id) as any,
        onChange: formikProps.handleChange,
        onBlur: formikProps.handleBlur,
        error: getProperty(formikProps.errors, id) != null,
        helperText: getProperty(formikProps.errors, id) as any,
    };
}
export function getProperty<T>(object: any, propertyName: string): T | undefined {
    return propertyName.split('.').reduce((ob, p) => ob?.[p] ?? null, object);
}

export function selectInputProps<T>(id: keyof T & string, formikProps: FormikProps<T>) {
    const handleChange = (e: any) => {
        formikProps.setFieldValue(id, e.target.value);
        formikProps.setFieldTouched(id, true, true)
    };

    return {
        id,
        onChange: handleChange as any,
        value: formikProps.values[id] as any,
        error: formikProps.errors[id] && formikProps.touched[id],
        helperText: formikProps.errors[id],
    };
}

export function datePickerProps<T>(id: keyof T & string, formikProps: FormikProps<T>) {
    const handleChange = (e: Date[]) => {
        formikProps.setFieldValue(id, e);
    };

    return {
        id,
        value: formikProps.values[id] as any,
        onChange: handleChange as any,
    };
}

export function datePickerErrorProps<T>(id: keyof T, formikProps: FormikProps<T>) {
    return {
        id,
        error: formikProps.errors[id] != null,
        helperText: formikProps.errors[id],
        onBlur: formikProps.handleBlur,
    };
}