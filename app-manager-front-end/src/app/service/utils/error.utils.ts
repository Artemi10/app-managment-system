
export const getErrorMessage = (error: { status: number; error: { message: string; }; }) => {
  if (isClientError(error)) {
    return  error.error.message;
  }
  else {
    return  'Service unavailable';
  }
}

export const isAuthenticationError =
  (error: { status: number; error: { message: string; }; }) => error.status === 401;

export const isAuthorizationError =
  (error: { status: number; error: { message: string; }; }) => error.status === 403;

export const isClientError =
  (error: { status: number; error: { message: string; }; }) =>
    error.status === 401 || error.status === 403 || error.status === 422 || error.status === 404;
