export enum StatType {
  HOURS = 'hours',
  DAYS = 'days',
  MONTHS = 'months'
}

export interface StatData {
  date: string,
  amount: number
}

export interface Stat {
  from: string,
  to: string,
  type: string
}
