export interface Event {
  id: number,
  name: string,
  extraInformation: string,
  time: string
}

export interface EventToAdd {
  name: string,
  extraInformation: string
}
