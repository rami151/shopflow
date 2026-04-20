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
  motDePasse: string;
}

export interface RegisterRequest {
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  role?: UserRole;
}

export interface JwtResponse {
  accessToken: string;
  refreshToken: string;
  email: string;
  role: string;
}

export interface ApiResponse<T> {
  data?: T;
  message?: string;
  errors?: string[];
}