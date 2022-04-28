import {BasicResponse} from "../dto/dto";
import {getMessageFromException} from "./getMessage";
import {CancellationToken} from "../components/shared/table/useFiltering";
import {useEffect, useState} from "react";

export function delay(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(() => resolve(), ms));
}

type Error = {
    message: string;
    exception: unknown;
}

type Return<T> = {
    isLoading: boolean,
    error: Error | null,
    data: Readonly<T> | null,
    reload: (data?: T) => void,
}

export function useApiRequest<T>(action: () => Promise<T>, dependencies?: unknown[]) : Return<T> {
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [data, setData] = useState<T | null>(null);
    const [error, setError] = useState<Error | null>(null);

    const reload = (data?: T) => {
        if (data != null) {
            setData(data);
            return () => {};
        }

        setIsLoading(true);
        setError(null);

        const token = new CancellationToken();

        action().then((r) => {
            if (token.continue()) {
                setData(r);
                setIsLoading(false);
            }
        }).catch((ex: unknown) => {
            getMessageFromException(ex).then((msg: string) => {
                if (token.continue()) {
                    setError({
                        message: msg,
                        exception: ex,
                    });
                    setIsLoading(false);
                }
            });
        });

        return () => token.cancel();
    };

    // eslint-disable-next-line react-hooks/exhaustive-deps
    useEffect(() => reload(), dependencies ?? []);

    return {
        isLoading,
        data,
        error,
        reload,
    };
}

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';

type ApiRequestOptions = {
    method: HttpMethod,
    path: string,
    body?: unknown,
    query?: Record<string, string | number | null | undefined>,
    minDelay?: number,
    mediaType?: string
};

type RequestOptions = ApiRequestOptions & {
    mediaType: string,
}

async function request(options: RequestOptions) {
    let query = '';

    if (options.query != null) {
        Object.keys(options.query)
            // eslint-disable-next-line no-param-reassign
            .forEach((key) => (options.query![key] == null ? delete options.query![key] : {}));
        query = `?${new URLSearchParams(options.query as any).toString()}`;
    }

    const responsePromise = fetch(options.path + query, {
        method: options.method,
        body: options.body != null ? JSON.stringify(options.body) : undefined,
        headers: {
            'Content-Type': options.mediaType,
        },
    });

    const response: Response = options.minDelay != null
        ? (await Promise.all([responsePromise, delay(options.minDelay)]))[0]
        : await responsePromise;

    if (response.status < 200 || response.status >= 300) {
        throw response;
    }

    return response;
}

async function apiRequest<TResponse>({ mediaType, ...options }: ApiRequestOptions) : Promise<TResponse> {
    const response = await request({
        mediaType: mediaType ?? 'application/json',
        ...options,
    });

    const responseText = await response.text();
    if (responseText !== '') {
        return JSON.parse(responseText);
    }

    return undefined as unknown as TResponse;
}

type RequestOptionsxd = {
    url: string;
    method: string;
    body?: string;
    minDuration?: number;
    credentials?: RequestCredentials;
    csvName?: string;
    resolveJsonPromise?: boolean;
}

function prepareHeaders() {
    return new Headers({
        'Content-Type': 'application/json',
    });
}

const minDelay = (time: number) => <T>(result: T) => new Promise<T>((resolve) => {
    if (!time || time < 0) {
        resolve(result);
    }
    setTimeout(() => resolve(result), time);
});

const basicRequest = <TRequest = BasicResponse>(options: RequestOptionsxd): Promise<TRequest> => {

    const headers = prepareHeaders();
    const defaults = {
        headers,
        credentials: 'same-origin' as RequestCredentials,
        resolveJsonPromise: true,
    };
    const finalOptions = { ...defaults, ...options };

    const startTime = Date.now();

    return fetch(options.url, finalOptions)
        .then(minDelay(startTime + (finalOptions.minDuration ?? 0) - Date.now()))
        .then((response) => {
            const contentType = response.headers.get('content-type');
            if (!response.ok) {
                throw response;
            }
            const isJson = contentType && contentType.indexOf('application/json') !== -1;
            if (isJson) {
                if (finalOptions.resolveJsonPromise) {
                    return response.json();
                }
                return response;
            }
            return Promise.resolve();
        });
};

export {
    request,
    apiRequest,
    basicRequest,
};
