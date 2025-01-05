export interface UserInfo {
  id: number;
  username: string;
  role: 'admin' | 'user';
  token: string;
}

export interface LoginForm {
  username: string;
  password: string;
}

export interface Role {
  id: number;
  name: string;
  permissions: string[];
}