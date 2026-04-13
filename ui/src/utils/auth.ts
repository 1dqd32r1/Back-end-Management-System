export function getToken() {
  return localStorage.getItem('Admin-Token');
}

export function setToken(token: string) {
  return localStorage.setItem('Admin-Token', token);
}

export function removeToken() {
  return localStorage.removeItem('Admin-Token');
}