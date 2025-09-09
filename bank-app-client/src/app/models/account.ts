import { Beneficiary } from './beneficiary';
import { Nominee } from './nominee';

export interface Account {
  accountId: number;
  interestRate: number;
  balance: number;
  dateOfOpening: string;
  accountType?: string | null;
  nominees: Nominee[];
  beneficiaries: Beneficiary[];
}
