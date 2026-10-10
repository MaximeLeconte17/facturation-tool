import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientDetailPageComponent } from './client-detail-page.component';

describe('ClientDetailPageComponent', () => {
  let component: ClientDetailPageComponent;
  let fixture: ComponentFixture<ClientDetailPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientDetailPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientDetailPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
