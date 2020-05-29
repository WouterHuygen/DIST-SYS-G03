export interface StatusObject<T> {
  succes: boolean;
  message: string;
  body: T;
}
