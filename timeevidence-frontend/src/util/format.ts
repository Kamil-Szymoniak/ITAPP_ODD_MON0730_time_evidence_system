import dayjs from 'dayjs';

export const DATE_TIME_API_FORMAT = 'YYYY-MM-DDTHH:mm';
export const DATE_TIME_LOCAL_FORMAT = 'DD.MM.YYYY HH:mm';

export const DATE_API_FORMAT = 'YYYY-MM-DD';
export const DATE_LOCAL_FORMAT = 'DD.MM.YYYY';

export function formatDate(value: unknown) {
    if (value == null || value === '') {
        return '';
    }
    return dayjs(`${value}`, DATE_API_FORMAT).format(DATE_LOCAL_FORMAT);
}

export function formatDateTime(value: unknown) {
    if (value == null || value === '') {
        return '';
    }
    return dayjs(`${value}`, DATE_TIME_API_FORMAT).format(DATE_TIME_LOCAL_FORMAT);
}

export function toTimestamp(dateString: string) {
    return dayjs(dateString, DATE_LOCAL_FORMAT).format(DATE_API_FORMAT);
}

export function formatAmount(n: number | null | unknown) {
    if (n == null || typeof n !== "number") {
        return '';
    }
    const hours = Math.floor(n / 60);
    let minutes = n % 60;
    minutes = minutes < 10 ? minutes : minutes;
    return `${hours}h ${minutes}m`;
}

export const futureFormattedDate = dayjs(new Date().setFullYear(2099, 11, 31)).toDate()

export function mapDate(param: string | null | undefined) {
    return param ? dayjs(param).toDate() : undefined
}

export function timeSince(dateTime?: string | unknown){
    if (dateTime == null || typeof dateTime !== "string") {
        return '';
    }

    const seconds = dayjs().diff(dayjs(dateTime, DATE_TIME_API_FORMAT), 'seconds');

    let interval = seconds / 31536000;

    if (interval > 1) {
        return ` ${Math.floor(interval)} year${interval >= 2 ? 's' : ''} ago`;
    }
    interval = seconds / 2592000;
    if (interval > 1) {
        return ` ${Math.floor(interval)} month${interval >= 2 ? 's' : ''} ago`;
    }
    interval = seconds / 86400;
    if (interval > 1) {
        return ` ${Math.floor(interval)} day${interval >= 2 ? 's' : ''} ago`;
    }
    interval = seconds / 3600;
    if (interval > 1) {
        return ` ${Math.floor(interval)} hour${interval >= 2 ? 's' : ''} ago`;
    }
    interval = seconds / 60;
    if (interval > 1) {
        return ` ${Math.floor(interval)} minute${interval >= 2 ? 's' : ''} ago`;
    }
    return ' less than a minute ago';
}