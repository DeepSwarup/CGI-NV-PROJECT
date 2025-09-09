export interface Nominee {
  nomineeId: number;
  name: string;
  govtId: string;
  govtIdType: string;
  phoneNo: string;
  relation: string;
  accountId: number;
}


export interface CreateNomineeRequest {
  name: string;
  govtId: string;
  govtIdType: string;
  phoneNo: string;
  relation: string;
  accountId: number;
}

export interface UpdateNomineeRequest extends CreateNomineeRequest {
  nomineeId: number;
}
