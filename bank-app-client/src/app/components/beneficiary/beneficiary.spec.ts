import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Beneficiary } from './beneficiary';

describe('Beneficiary', () => {
  let component: Beneficiary;
  let fixture: ComponentFixture<Beneficiary>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Beneficiary]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Beneficiary);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
