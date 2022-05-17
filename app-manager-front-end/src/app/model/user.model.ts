export enum Authority {
  ACTIVE = 'ACTIVE',
  UPDATE_CONFIRMED = 'UPDATE_CONFIRMED',
  UPDATE_NOT_CONFIRMED = 'UPDATE_NOT_CONFIRMED'
}

export interface UserToUpdate {
  newPassword: string,
  rePassword: string,
}
