export interface User {
  id: number;
  email: string;
  nom: string;
  prenom: string;
  role: UserRole;
  active: boolean;
  createdAt?: string;
}

export enum UserRole {
  CUSTOMER = 'CUSTOMER',
  SELLER = 'SELLER',
  ADMIN = 'ADMIN'
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  nom: string;
  prenom: string;
  role?: UserRole;
}

export interface JwtResponse {
  access_token: string;
  refresh_token: string;
  expires_in: number;
}

export interface ApiResponse<T> {
  data?: T;
  message?: string;
  errors?: string[];
}