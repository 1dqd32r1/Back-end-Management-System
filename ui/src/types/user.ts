export interface User {
  id?: number;
  username: string;
  email?: string;
  phone?: string;
  status?: string;
  nickname?: string;
  avatar?: string;
  role?: string;
  createTime?: string;
  updateTime?: string;
  lastLoginTime?: string;
}

export interface UserQueryParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;
  keyword?: string;
  status?: string;
}

export interface UserPageResponse {
  content: User[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}

export interface UpdatePasswordRequest {
  oldPassword: string;
  newPassword: string;
}
