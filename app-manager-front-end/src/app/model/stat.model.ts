export enum StatType {
  HOUR = 'HOUR',
  DAY = 'DAY',
  MONTH = 'MONTH'
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
