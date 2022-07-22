export interface App {
  id: number,
  name: string,
  creationTime: string
}

export interface AppToCreate {
  name: string
}

export enum OrderType {
  ASC = "ASC",
  DESC = "DESC"
}
