import { Beneficiary } from './beneficiary';
import { Nominee } from './nominee';

export interface Account {
  accountId: number;
  interestRate: number;
  balance: number;
  dateOfOpening: string;
  accountType?: 'SAVINGS' | 'TERM' | null;
  nominees: Nominee[];
  beneficiaries: Beneficiary[];
  status?: 'PENDING' | 'ACTIVE' | 'CLOSED' | 'DECLINED';
}
