import { TestBed } from '@angular/core/testing';

import { Nominee } from './nominee';

describe('Nominee', () => {
  let service: Nominee;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Nominee);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
