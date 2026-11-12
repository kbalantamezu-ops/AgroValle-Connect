import { sesion } from './sesion.js';

const API_BASE = '/api/v1';

async function apiFetch(path, options = {}) {
    const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
    const token = sesion.token();
    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }
    const config = { ...options, headers };
    const response = await fetch(`${API_BASE}${path}`, config);
    const body = response.status === 204 ? null : await response.json().catch(() => null);
    if (!response.ok) {
        let mensaje = `Error ${response.status}`;
        if (body && body.message) {
            mensaje = body.message;
        }
        if (body && body.fieldErrors) {
            const campos = Object.entries(body.fieldErrors)
                .map(([campo, error]) => `${campo}: ${error}`)
                .join('. ');
            if (campos) {
                mensaje = `${mensaje}. ${campos}`;
            }
        }
        const error = new Error(mensaje);
        error.status = response.status;
        throw error;
    }
    return body;
}

export const api = {
    get: (path) => apiFetch(path),
    post: (path, data) => apiFetch(path, { method: 'POST', body: JSON.stringify(data) }),
    put: (path, data) => apiFetch(path, { method: 'PUT', body: JSON.stringify(data) }),
    patch: (path, data) => apiFetch(path, { method: 'PATCH', body: JSON.stringify(data) }),
    delete: (path) => apiFetch(path, { method: 'DELETE' }),
};
