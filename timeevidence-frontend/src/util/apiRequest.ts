import {BasicResponse} from "../dto/dto";

export function delay(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(() => resolve(), ms));
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
