export type TransactionTypeBackend = 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER';
export type TransactionStatusBackend = 'SUCCESS' | 'FAILED' | string;

export interface Transaction {
  transactionId: number;
  accountId: number;
  amount: number; 
  transactiontype: TransactionTypeBackend;
  transactionstatus?: TransactionStatusBackend;
  transactionDateandTime: string;
  transactionRemarks?: string;
}

export const isCreditLike = (type?: TransactionTypeBackend) =>
  !!type && (type === 'DEPOSIT');

export const isDebitLike = (type?: TransactionTypeBackend) =>
  !!type && (type === 'WITHDRAWAL');

export const badgeFor = (type?: TransactionTypeBackend) => {
  if (!type) return { label: 'Unknown', color: 'gray' };
  if (isCreditLike(type)) return { label: 'Deposit', color: 'green' };
  if (isDebitLike(type)) return { label: 'Withdrawal', color: 'red' };
  return { label: 'Transfer', color: 'blue' };
};