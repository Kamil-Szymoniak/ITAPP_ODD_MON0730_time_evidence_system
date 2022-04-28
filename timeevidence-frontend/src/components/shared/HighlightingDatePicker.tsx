import React, {useEffect, useState} from 'react';
import LocalizationProvider from "@mui/lab/LocalizationProvider";
import AdapterDateFns from "@mui/lab/AdapterDateFns";
import {TextField, Box, Badge} from "@mui/material";
import {CalendarPickerSkeleton, PickersDay, StaticDatePicker} from "@mui/lab";

type Props = {
  value: Date | null;
  onChange: (date: (Date | null), selectionState?: string) => void //TDate instead of null check type/create interface
  fetchFunction: (month: number) => Promise<any>;
  minDate?: any;
  maxDate?: any;
  label: string;
  error?: boolean;
  helperText?: React.ReactNode;
  onBlur?: ((event: any) => void);
  disabled?: boolean;
  readOnly?: boolean;
  style?: any;
  reload?: number;
}

const getColor = (val: string) => {
  const minutes = parseInt(val)
  if (minutes < 480) return '#40BA90'
  if (minutes === 480) return '#60EF30'
  if (minutes < 600) return '#FFFF00'
  if (minutes < 720) return '#FF9900'
  return '#FF1010'
}

export function HighlightingDatePicker(props: Props) {
  const requestAbortController = React.useRef<AbortController | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [currentMonth, setCurrentMonth] = useState<Date>(new Date())
  const [highlightedDays, setHighlightedDays] = useState<Map<string, string> | null>(null);
  
  useEffect(() => {
    fetchHighlightedDays(new Date());
    return () => requestAbortController.current?.abort();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  
  useEffect(() => {
    if (requestAbortController.current) {
      requestAbortController.current.abort();
    }
    
    setIsLoading(true);
    setHighlightedDays(null);
    fetchHighlightedDays(currentMonth);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [props.reload])
  
  const fetchHighlightedDays = (date: Date) => {
    const controller = new AbortController();
    props.fetchFunction(date.getMonth() + 1)
      .then((map) => {
        setHighlightedDays(new Map(Object.entries(map)));
        setIsLoading(false);
      })
      .catch((error) => {
        if (error.name !== 'AbortError') {
          throw error;
        }
      });
    requestAbortController.current = controller;
  };
  
  const handleMonthChange = (date: Date) => {
    if (requestAbortController.current) {
      requestAbortController.current.abort();
    }
    
    setIsLoading(true);
    setHighlightedDays(null);
    fetchHighlightedDays(date);
  }
  
  return (
    <>
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <StaticDatePicker<Date>
          orientation="landscape"
          openTo="day"
          disabled={props.disabled}
          readOnly={props.readOnly}
          value={props.value}
          inputFormat="dd.MM.yyyy"
          mask="__.__.____"
          loading={isLoading}
          minDate={props.minDate}
          maxDate={props.maxDate}
          onChange={(val, state) => {
            props.onChange(val, state)
            setCurrentMonth(val ?? new Date())
          }}
          onMonthChange={handleMonthChange}
          renderInput={(params) =>
            <TextField
              {...params}
              margin={"normal"}
              error={props.error}
              helperText={props.error ? props.helperText : ''}
              onBlur={props.onBlur}
            />
          }
          renderLoading={() => <CalendarPickerSkeleton/>}
          renderDay={(day, _value, DayComponentProps) => {
            const isSelected =
              !DayComponentProps.outsideCurrentMonth &&
              (highlightedDays?.get(day.getDate().toString()) ?? 0) > 0;
            
            return (
              <Badge
                key={day.toString()}
                overlap="circular"
                badgeContent={isSelected ? (
                  <Box component="span" sx={{
                    bgcolor: `${getColor(highlightedDays!.get(day.getDate().toString())!)}`,
                    width: 10,
                    height: 10,
                    borderRadius: '50%'
                  }}/>
                ) : undefined}
              >
                <PickersDay {...DayComponentProps} />
              </Badge>
            );
          }}
        />
      </LocalizationProvider>
    </>
  );
}