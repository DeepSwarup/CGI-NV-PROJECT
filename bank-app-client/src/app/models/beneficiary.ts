export interface Beneficiary {
  beneficiaryId: number;
  beneficiaryName: string;
  beneficiaryAccNo: number;
  ifsc: string;
  accountType: string;
  accountId: number;
}

export interface CreateBeneficiaryRequest {
  beneficiaryName: string;
  beneficiaryAccNo: number;
  ifsc: string;
  accountType: string;
  accountId: number;
}

export interface UpdateBeneficiaryRequest extends CreateBeneficiaryRequest {
  beneficiaryId: number;
}
