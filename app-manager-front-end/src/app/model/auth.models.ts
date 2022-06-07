export interface LogInModel {
  email: string,
  password: string
}

export interface SignUpModel {
  email: string,
  password: string,
  rePassword: string
}

export interface Token {
  accessToken: string,
  refreshToken: string
}

export interface AccessToken {
  accessToken: string
}

export interface EnterToken {
  enterToken: string
}
