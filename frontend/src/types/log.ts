export interface OperationLog {
  id: number;
  username: string;
  operation: string;
  method: string;
  params: string;
  result: string;
  ip: string;
  createTime: string;
}
