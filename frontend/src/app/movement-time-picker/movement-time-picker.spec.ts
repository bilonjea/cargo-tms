import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MovementTimePicker } from './movement-time-picker';

describe('MovementTimePicker', () => {
  let component: MovementTimePicker;
  let fixture: ComponentFixture<MovementTimePicker>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MovementTimePicker]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MovementTimePicker);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
