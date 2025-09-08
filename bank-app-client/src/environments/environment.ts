export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080/bank-api',
  customersUrl: 'http://localhost:8080/bank-api/customers',
  accountsUrl: 'http://localhost:8080/bank-api/accounts/customer',
  nomineesUrl: 'http://localhost:8080/bank-api/nominees',
  beneficiariesUrl: 'http://localhost:8080/bank-api/beneficiaries',

  validation: {
    maxNomineesPerAccount: 4,
    maxBeneficiariesPerAccount: 10
  },
  ui: {
    searchDelay: 500
  }
};
