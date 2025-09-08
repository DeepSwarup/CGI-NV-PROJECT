import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Nominee } from './nominee';

describe('Nominee', () => {
  let component: Nominee;
  let fixture: ComponentFixture<Nominee>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Nominee]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Nominee);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
