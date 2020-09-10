import { ActivatedRoute, Params, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import {
  NgbDateAdapter,
  NgbDateNativeAdapter
} from '@ng-bootstrap/ng-bootstrap';

import { HttpErrorResponse } from '@angular/common/http';
import { RestSourceUser } from '../../models/rest-source-user.model';
import { RestSourceUserService } from '../../services/rest-source-user.service';
import { SourceClientAuthorizationService } from '../../services/source-client-authorization.service';
import {RestSourceProject} from "../../models/rest-source-project.model";
import {Location} from '@angular/common';

@Component({
  selector: 'update-rest-source-user',
  templateUrl: './update-rest-source-user.component.html',
  providers: [{ provide: NgbDateAdapter, useClass: NgbDateNativeAdapter }]
})

export class UpdateRestSourceUserComponent implements OnInit {
  isEditing = false;
  errorMessage?: string;
  startDate: Date;
  endDate: Date;
  restSourceUser: RestSourceUser;
  restSourceUsers: any[] = [];
  restSourceProjects: RestSourceProject[] = [];

  constructor(
    private restSourceUserService: RestSourceUserService,
    private sourceClientAuthorizationService: SourceClientAuthorizationService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private _location: Location
  ) {}

  ngOnInit() {
    this.loadAllRestSourceProjects()
  }

  initialize(){
    this.activatedRoute.params.subscribe(params => {
      if (params.hasOwnProperty('id')) {
        this.restSourceUserService.getUserById(params['id']).subscribe(
          user => {
            this.restSourceUser = user;
            this.isEditing = true;
            this.startDate = new Date(this.restSourceUser.startDate);
            this.endDate = new Date(this.restSourceUser.endDate);
            this.onChangeProject(this.restSourceUser.projectId)
          },
          (err: Response) => {
            console.log('Cannot retrieve current user details', err)
            this.errorMessage = 'Cannot retrieve current user details';
            window.setTimeout(() => this.router.navigate(['']), 5000);
          }
        );
      } else {
        this.activatedRoute.queryParams.subscribe((params: Params) => {
          if (params.hasOwnProperty('error')) {
            this.errorMessage = params['error_description'];
          } else {
            this.errorMessage = null;
            this.addRestSourceUser(params['code'], params['state']);
          }
        });
      }
    });
  }

  updateRestSourceUser() {
    if(!this.endDate || !this.startDate){
      this.errorMessage =
        'Please select Start Date and End Date';
      return;
    }
    try{
      this.restSourceUser.startDate = this.startDate.toISOString();
      this.restSourceUser.endDate = this.endDate.toISOString();
    }catch(err){
      this.errorMessage =
        'Please enter valid Start Date and End Date';
      return;
    }
    this.restSourceUserService.updateUser(this.restSourceUser).subscribe(
      () => {
        return this.router.navigate(['/users']);
      },
      (err: HttpErrorResponse) => {
        if (err.error instanceof ErrorEvent) {
          // A client-side or network error occurred. Handle it accordingly.
          this.errorMessage =
            'Something went wrong. Please check your connection.';
        } else {
          // The backend returned an unsuccessful response code.
          // The response body may contain clues as to what went wrong,
          this.errorMessage = `Backend Error: Status=${err.status},
          Body: ${err.error.error}, ${err.error.message}`;
          if (err.status == 417) {
            this.errorMessage +=
              ' Please check the details are correct and try again.';
          }
        }
      }
    );
  }

  private addRestSourceUser(code: string, state: string) {
    this.restSourceUserService.addAuthorizedUser(code, state).subscribe(
      data => {
        this.onChangeProject(data.projectId)
        this.restSourceUser = data;
      },
      (err: Response) => {
        console.log('Cannot retrieve current user details', err)
        this.errorMessage = 'Cannot retrieve current user details';
        window.setTimeout(() => this.router.navigate(['']), 5000);
      }
    );
  }

  private loadAllRestSourceProjects() {
    this.restSourceUserService.getAllProjects().subscribe(
      (data: any) => {
        this.restSourceProjects = data.projects;
        this.initialize()
      },
      () => {
        this.errorMessage = 'Cannot load projects!';
      }
    );
  }

  onChangeProject(projectId: string) {
    if(projectId === ''){
    }else{
      this.loadAllRestSourceUsersOfProject(projectId)
    }
  }

  private loadAllRestSourceUsersOfProject(projectId: string) {
    this.restSourceUserService.getAllUsersOfProject(projectId).subscribe(
      (data: any) => {
        this.restSourceUsers = data.users;
      },
      () => {
        this.errorMessage = 'Cannot load registered users!';
      }
    );
  }

  cancelUpdateUser() {
    return this.router.navigate(['/users']);
  }

  backClicked() {
    this._location.back();
  }
}
